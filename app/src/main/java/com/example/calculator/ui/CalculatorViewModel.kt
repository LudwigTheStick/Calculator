package com.example.calculator.ui

import androidx.lifecycle.ViewModel
import com.example.calculator.domain.BinaryOperator
import com.example.calculator.domain.CalculatorEngine
import com.example.calculator.domain.HistoryItem
import com.example.calculator.domain.UnaryOperator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val primaryDisplay: String = "0",
    val secondaryDisplay: String = "",
    val hasError: Boolean = false,
    val errorStringRes: String? = null,
    val isCalculated: Boolean = false,
    val history: List<HistoryItem> = emptyList(),
    val isHistoryOpen: Boolean = false
)

class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        syncState()
    }

    private fun syncState() {
        val state = engine.getState()
        _uiState.update { current ->
            current.copy(
                primaryDisplay = state.primaryDisplay,
                secondaryDisplay = state.secondaryDisplay,
                hasError = state.hasError,
                errorStringRes = state.errorStringRes,
                isCalculated = state.isCalculated,
                history = engine.history
            )
        }
    }

    fun onDigit(digit: Char) {
        engine.onDigit(digit)
        syncState()
    }

    fun onDecimal() {
        engine.onDecimal()
        syncState()
    }

    fun onBackspace() {
        engine.onBackspace()
        syncState()
    }

    fun onClear() {
        engine.onClear()
        syncState()
    }

    fun onClearEntry() {
        engine.onClearEntry()
        syncState()
    }

    fun onNegate() {
        engine.onNegate()
        syncState()
    }

    fun onBinaryOperator(op: BinaryOperator) {
        engine.onBinaryOperator(op)
        syncState()
    }

    fun onUnary(op: UnaryOperator) {
        engine.onUnary(op)
        syncState()
    }

    fun onPercent() {
        engine.onPercent()
        syncState()
    }

    fun onEquals() {
        engine.onEquals()
        syncState()
    }

    fun onToggleHistory() {
        _uiState.update { it.copy(isHistoryOpen = !it.isHistoryOpen) }
    }

    fun onCloseHistory() {
        _uiState.update { it.copy(isHistoryOpen = false) }
    }

    fun onRecallHistory(item: HistoryItem) {
        engine.recallHistory(item)
        syncState()
        _uiState.update { it.copy(isHistoryOpen = false) }
    }

    fun onClearHistory() {
        engine.clearHistory()
        syncState()
    }
}
