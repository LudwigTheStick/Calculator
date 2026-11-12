package com.example.calculator.domain

enum class BinaryOperator(val symbol: String, val precedence: Int) {
    ADD("+", 1),
    SUBTRACT("−", 1),
    MULTIPLY("×", 2),
    DIVIDE("÷", 2);

    companion object {
        fun fromSymbol(symbol: String): BinaryOperator? = entries.find { it.symbol == symbol }
    }
}

enum class UnaryOperator(val symbol: String) {
    SQUARE("sqr"),
    SQRT("√"),
    RECIPROCAL("1/"),
    NEGATE("+/-")
}
