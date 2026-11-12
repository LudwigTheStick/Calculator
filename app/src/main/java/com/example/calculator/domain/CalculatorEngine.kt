package com.example.calculator.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

sealed class ExpressionToken {
    data class Value(val number: BigDecimal, val display: String) : ExpressionToken()
    data class Op(val operator: BinaryOperator) : ExpressionToken()
}

data class CalculatorState(
    val primaryDisplay: String = "0",
    val secondaryDisplay: String = "",
    val hasError: Boolean = false,
    val errorStringRes: String? = null,
    val isCalculated: Boolean = false
)

class CalculatorEngine {

    private var currentInput: String = "0"
    private val expressionTokens = mutableListOf<ExpressionToken>()
    private var isNewInput: Boolean = true
    private var isCalculated: Boolean = false
    private var hasError: Boolean = false
    private var errorStringRes: String? = null

    private val _history = mutableListOf<HistoryItem>()
    val history: List<HistoryItem> get() = _history

    fun getState(): CalculatorState {
        val secondary = if (isCalculated) {
            // Already includes trailing "="
            buildSecondaryDisplay()
        } else {
            buildSecondaryDisplay()
        }

        return CalculatorState(
            primaryDisplay = currentInput,
            secondaryDisplay = secondary,
            hasError = hasError,
            errorStringRes = errorStringRes,
            isCalculated = isCalculated
        )
    }

    fun onDigit(digit: Char) {
        if (!digit.isDigit()) return

        if (hasError || isCalculated) {
            clearAll()
            currentInput = digit.toString()
            isNewInput = false
            return
        }

        if (isNewInput) {
            currentInput = digit.toString()
            isNewInput = false
        } else {
            // Count only digit characters (exclude sign and decimal point) for the limit
            val digitCount = currentInput.count { it.isDigit() }
            if (digitCount >= MAX_INPUT_LENGTH) return

            if (currentInput == "0") {
                currentInput = digit.toString()
            } else if (currentInput == "-0") {
                currentInput = "-$digit"
            } else {
                currentInput += digit
            }
        }
    }

    fun onDecimal() {
        if (hasError || isCalculated) {
            clearAll()
            currentInput = "0."
            isNewInput = false
            return
        }

        if (isNewInput) {
            currentInput = "0."
            isNewInput = false
        } else if (!currentInput.contains('.')) {
            val digitCount = currentInput.count { it.isDigit() }
            if (digitCount >= MAX_INPUT_LENGTH) return
            currentInput += "."
        }
    }

    fun onBackspace() {
        if (hasError || isCalculated || isNewInput) return

        if (currentInput.length > 1) {
            currentInput = currentInput.dropLast(1)
            if (currentInput == "-" || currentInput == "-0") {
                currentInput = "0"
                isNewInput = true
            }
        } else {
            currentInput = "0"
            isNewInput = true
        }
    }

    fun onClear() {
        clearAll()
    }

    fun onClearEntry() {
        if (hasError) {
            clearAll()
        } else {
            currentInput = "0"
            isNewInput = true
        }
    }

    fun onNegate() {
        if (hasError) return

        val value = parseBigDecimal(currentInput)
        if (value.compareTo(BigDecimal.ZERO) == 0) return

        val negated = value.negate()
        currentInput = formatBigDecimal(negated)

        if (isCalculated) {
            expressionTokens.clear()
            isCalculated = false
        }
    }

    fun onBinaryOperator(operator: BinaryOperator) {
        if (hasError) return

        if (isCalculated) {
            val prevResult = parseBigDecimal(currentInput)
            expressionTokens.clear()
            expressionTokens.add(ExpressionToken.Value(prevResult, formatBigDecimal(prevResult)))
            expressionTokens.add(ExpressionToken.Op(operator))
            isCalculated = false
            isNewInput = true
            return
        }

        if (isNewInput && expressionTokens.isNotEmpty() && expressionTokens.last() is ExpressionToken.Op) {
            // Replace the last operator if user changes their mind
            expressionTokens[expressionTokens.lastIndex] = ExpressionToken.Op(operator)
            return
        }

        val value = parseBigDecimal(currentInput)
        expressionTokens.add(ExpressionToken.Value(value, formatBigDecimal(value)))
        expressionTokens.add(ExpressionToken.Op(operator))
        isNewInput = true
    }

    fun onEquals() {
        if (hasError || expressionTokens.isEmpty() || isCalculated) return

        val value = parseBigDecimal(currentInput)
        expressionTokens.add(ExpressionToken.Value(value, formatBigDecimal(value)))

        val expressionString = buildSecondaryDisplay() + " ="

        try {
            val result = evaluateTokens(expressionTokens)
            val resultStr = formatBigDecimal(result)
            currentInput = resultStr
            isCalculated = true
            isNewInput = true

            // Add to history
            _history.add(0, HistoryItem(expression = expressionString, result = resultStr))
        } catch (e: ArithmeticException) {
            hasError = true
            errorStringRes = if (e.message?.contains("zero", ignoreCase = true) == true) {
                "error_divide_by_zero"
            } else {
                "error_invalid_input"
            }
            currentInput = ""
        }
    }

    fun onUnary(operator: UnaryOperator) {
        if (hasError) return

        val value = parseBigDecimal(currentInput)
        try {
            val (result, label) = when (operator) {
                UnaryOperator.SQUARE -> {
                    val res = value.multiply(value).stripTrailingZeros()
                    res to "sqr(${formatBigDecimal(value)})"
                }
                UnaryOperator.SQRT -> {
                    if (value < BigDecimal.ZERO) throw ArithmeticException("Square root of negative number")
                    val res = sqrtBigDecimal(value)
                    res to "√(${formatBigDecimal(value)})"
                }
                UnaryOperator.RECIPROCAL -> {
                    if (value.compareTo(BigDecimal.ZERO) == 0) {
                        throw ArithmeticException("Cannot divide by zero")
                    }
                    val res = BigDecimal.ONE.divide(value, MathContext(16, RoundingMode.HALF_UP))
                        .setScale(12, RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                    res to "1/(${formatBigDecimal(value)})"
                }
                UnaryOperator.NEGATE -> {
                    val res = value.negate()
                    res to "negate(${formatBigDecimal(value)})"
                }
            }

            currentInput = formatBigDecimal(result)
            isNewInput = true

            if (isCalculated) {
                expressionTokens.clear()
                expressionTokens.add(ExpressionToken.Value(result, label))
                isCalculated = false
            }
        } catch (e: ArithmeticException) {
            hasError = true
            errorStringRes = if (e.message?.contains("zero", ignoreCase = true) == true) {
                "error_divide_by_zero"
            } else {
                "error_invalid_input"
            }
            currentInput = ""
        }
    }

    fun onPercent() {
        if (hasError) return

        val currentVal = parseBigDecimal(currentInput)
        // Find if there is a preceding binary operator
        val lastOpIndex = expressionTokens.indexOfLast { it is ExpressionToken.Op }

        if (lastOpIndex != -1) {
            val lastOp = (expressionTokens[lastOpIndex] as ExpressionToken.Op).operator
            // The base operand is the value before this operator
            val baseToken = expressionTokens.subList(0, lastOpIndex).lastOrNull { it is ExpressionToken.Value }
            val baseValue = (baseToken as? ExpressionToken.Value)?.number ?: BigDecimal.ZERO

            val percentResult = when (lastOp) {
                BinaryOperator.ADD, BinaryOperator.SUBTRACT -> {
                    // base * (currentVal / 100)
                    baseValue.multiply(currentVal).divide(BigDecimal(100), MathContext(16, RoundingMode.HALF_UP))
                }
                BinaryOperator.MULTIPLY, BinaryOperator.DIVIDE -> {
                    // currentVal / 100
                    currentVal.divide(BigDecimal(100), MathContext(16, RoundingMode.HALF_UP))
                }
            }.setScale(12, RoundingMode.HALF_UP).stripTrailingZeros()

            currentInput = formatBigDecimal(percentResult)
            isNewInput = true
        } else {
            // No preceding operator -> 0
            currentInput = "0"
            isNewInput = true
        }
    }

    fun recallHistory(item: HistoryItem) {
        clearAll()
        currentInput = item.result
        isNewInput = true
        isCalculated = true
    }

    fun clearHistory() {
        _history.clear()
    }

    private fun clearAll() {
        currentInput = "0"
        expressionTokens.clear()
        isNewInput = true
        isCalculated = false
        hasError = false
        errorStringRes = null
    }

    private fun buildSecondaryDisplay(): String {
        return expressionTokens.joinToString(" ") { token ->
            when (token) {
                is ExpressionToken.Value -> token.display
                is ExpressionToken.Op -> token.operator.symbol
            }
        }
    }

    private fun evaluateTokens(tokens: List<ExpressionToken>): BigDecimal {
        val values = ArrayDeque<BigDecimal>()
        val operators = ArrayDeque<BinaryOperator>()

        fun applyTopOp() {
            if (values.size < 2 || operators.isEmpty()) return
            val right = values.removeLast()
            val left = values.removeLast()
            val op = operators.removeLast()

            val res = when (op) {
                BinaryOperator.ADD -> left.add(right)
                BinaryOperator.SUBTRACT -> left.subtract(right)
                BinaryOperator.MULTIPLY -> left.multiply(right)
                BinaryOperator.DIVIDE -> {
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        throw ArithmeticException("Cannot divide by zero")
                    }
                    left.divide(right, MathContext(16, RoundingMode.HALF_UP))
                }
            }
            values.addLast(res)
        }

        for (token in tokens) {
            when (token) {
                is ExpressionToken.Value -> values.addLast(token.number)
                is ExpressionToken.Op -> {
                    while (operators.isNotEmpty() && operators.last().precedence >= token.operator.precedence) {
                        applyTopOp()
                    }
                    operators.addLast(token.operator)
                }
            }
        }

        while (operators.isNotEmpty()) {
            applyTopOp()
        }

        val finalValue = values.firstOrNull() ?: BigDecimal.ZERO
        return finalValue.setScale(12, RoundingMode.HALF_UP).stripTrailingZeros()
    }

    companion object {
        /** Maximum number of digits (excluding negative sign and decimal point) allowed in input */
        private const val MAX_INPUT_LENGTH = 16

        fun formatBigDecimal(value: BigDecimal): String {
            var stripped = value.stripTrailingZeros()
            if (stripped.scale() < 0) {
                stripped = stripped.setScale(0)
            }
            return stripped.toPlainString()
        }

        fun parseBigDecimal(str: String): BigDecimal {
            if (str.isBlank()) return BigDecimal.ZERO
            val clean = str.replace(',', '.').trim()
            return try {
                BigDecimal(clean)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
        }

        fun sqrtBigDecimal(value: BigDecimal, precision: Int = 16): BigDecimal {
            if (value < BigDecimal.ZERO) throw ArithmeticException("Square root of negative number")
            if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO

            val mc = MathContext(precision + 4, RoundingMode.HALF_UP)
            val two = BigDecimal(2)
            var x0 = BigDecimal.ZERO
            var x1 = BigDecimal(Math.sqrt(value.toDouble()))
            if (x1.compareTo(BigDecimal.ZERO) == 0) {
                x1 = BigDecimal("0.1")
            }

            val epsilon = BigDecimal.ONE.movePointLeft(precision)
            var iterations = 0
            while (iterations < 100) {
                x0 = x1
                x1 = value.divide(x0, mc).add(x0).divide(two, mc)
                if ((x1 - x0).abs() < epsilon) {
                    break
                }
                iterations++
            }
            return x1.setScale(12, RoundingMode.HALF_UP).stripTrailingZeros()
        }
    }
}
