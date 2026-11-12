package com.example.calculator

import com.example.calculator.domain.BinaryOperator
import com.example.calculator.domain.CalculatorEngine
import com.example.calculator.domain.UnaryOperator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setup() {
        engine = CalculatorEngine()
    }

    @Test
    fun testSimpleAddition() {
        engine.onDigit('5')
        engine.onBinaryOperator(BinaryOperator.ADD)
        engine.onDigit('7')
        engine.onEquals()

        val state = engine.getState()
        assertEquals("12", state.primaryDisplay)
        assertFalse(state.hasError)
        assertEquals(1, engine.history.size)
        assertEquals("12", engine.history[0].result)
    }

    @Test
    fun testAlgebraicPrecedence() {
        // 2 + 3 * 4 = 14
        engine.onDigit('2')
        engine.onBinaryOperator(BinaryOperator.ADD)
        engine.onDigit('3')
        engine.onBinaryOperator(BinaryOperator.MULTIPLY)
        engine.onDigit('4')
        engine.onEquals()

        val state = engine.getState()
        assertEquals("14", state.primaryDisplay)
    }

    @Test
    fun testSubtractionAndDivision() {
        // 20 - 10 / 2 = 15
        engine.onDigit('2')
        engine.onDigit('0')
        engine.onBinaryOperator(BinaryOperator.SUBTRACT)
        engine.onDigit('1')
        engine.onDigit('0')
        engine.onBinaryOperator(BinaryOperator.DIVIDE)
        engine.onDigit('2')
        engine.onEquals()

        val state = engine.getState()
        assertEquals("15", state.primaryDisplay)
    }

    @Test
    fun testSquare() {
        engine.onDigit('6')
        engine.onUnary(UnaryOperator.SQUARE)

        val state = engine.getState()
        assertEquals("36", state.primaryDisplay)
    }

    @Test
    fun testSquareRoot() {
        engine.onDigit('8')
        engine.onDigit('1')
        engine.onUnary(UnaryOperator.SQRT)

        val state = engine.getState()
        assertEquals("9", state.primaryDisplay)
    }

    @Test
    fun testReciprocal() {
        engine.onDigit('4')
        engine.onUnary(UnaryOperator.RECIPROCAL)

        val state = engine.getState()
        assertEquals("0.25", state.primaryDisplay)
    }

    @Test
    fun testNegate() {
        engine.onDigit('9')
        engine.onNegate()
        assertEquals("-9", engine.getState().primaryDisplay)
        engine.onNegate()
        assertEquals("9", engine.getState().primaryDisplay)
    }

    @Test
    fun testPercentWithAddition() {
        // 200 + 10% = 220
        engine.onDigit('2')
        engine.onDigit('0')
        engine.onDigit('0')
        engine.onBinaryOperator(BinaryOperator.ADD)
        engine.onDigit('1')
        engine.onDigit('0')
        engine.onPercent()
        assertEquals("20", engine.getState().primaryDisplay)
        engine.onEquals()
        assertEquals("220", engine.getState().primaryDisplay)
    }

    @Test
    fun testPercentWithMultiplication() {
        // 200 * 10% = 20
        engine.onDigit('2')
        engine.onDigit('0')
        engine.onDigit('0')
        engine.onBinaryOperator(BinaryOperator.MULTIPLY)
        engine.onDigit('1')
        engine.onDigit('0')
        engine.onPercent()
        assertEquals("0.1", engine.getState().primaryDisplay)
        engine.onEquals()
        assertEquals("20", engine.getState().primaryDisplay)
    }

    @Test
    fun testBackspace() {
        engine.onDigit('1')
        engine.onDigit('2')
        engine.onDigit('3')
        engine.onBackspace()
        assertEquals("12", engine.getState().primaryDisplay)
        engine.onBackspace()
        assertEquals("1", engine.getState().primaryDisplay)
        engine.onBackspace()
        assertEquals("0", engine.getState().primaryDisplay)
    }

    @Test
    fun testClearEntryVsClear() {
        engine.onDigit('5')
        engine.onBinaryOperator(BinaryOperator.ADD)
        engine.onDigit('9')
        engine.onClearEntry()
        assertEquals("0", engine.getState().primaryDisplay)

        engine.onDigit('4')
        engine.onEquals()
        assertEquals("9", engine.getState().primaryDisplay)

        engine.onClear()
        assertEquals("0", engine.getState().primaryDisplay)
        assertEquals("", engine.getState().secondaryDisplay)
    }

    @Test
    fun testDivideByZero() {
        engine.onDigit('7')
        engine.onBinaryOperator(BinaryOperator.DIVIDE)
        engine.onDigit('0')
        engine.onEquals()

        val state = engine.getState()
        assertTrue(state.hasError)
        assertEquals("error_divide_by_zero", state.errorStringRes)
    }

    @Test
    fun testHistoryRecallAndClear() {
        engine.onDigit('3')
        engine.onBinaryOperator(BinaryOperator.ADD)
        engine.onDigit('4')
        engine.onEquals()

        assertEquals(1, engine.history.size)
        val item = engine.history[0]
        assertEquals("7", item.result)

        engine.onDigit('9')
        engine.recallHistory(item)
        assertEquals("7", engine.getState().primaryDisplay)

        engine.clearHistory()
        assertEquals(0, engine.history.size)
    }
}
