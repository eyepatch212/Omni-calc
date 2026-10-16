package com.example.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository: HistoryRepository
    private val evaluator = CalculatorEvaluator()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        historyRepository = HistoryRepository(db.calculationDao())

        viewModelScope.launch {
            historyRepository.allHistory.collect { historyList ->
                _uiState.update { it.copy(recentHistory = historyList) }
            }
        }
    }

    fun onDigit(digit: String) {
        val current = _uiState.value.expression
        val newExpr = if (_uiState.value.displayResult != null && _uiState.value.errorMessage == null) {
            // If user just got a result and starts typing a digit, start fresh
            digit
        } else {
            current + digit
        }
        updateExpression(newExpr)
    }

    fun onOperator(op: String) {
        val current = _uiState.value.expression
        val newExpr = if (_uiState.value.displayResult != null && _uiState.value.errorMessage == null) {
            // Continue calculating with the previous result
            val prevResult = _uiState.value.displayResult ?: ""
            prevResult + op
        } else if (current.isEmpty()) {
            if (op == "−" || op == "-") "−" else ""
        } else {
            val lastChar = current.last()
            if (lastChar in listOf('+', '−', '×', '÷', '^', '%')) {
                current.dropLast(1) + op
            } else {
                current + op
            }
        }
        updateExpression(newExpr)
    }

    fun onDecimal() {
        val current = _uiState.value.expression
        if (_uiState.value.displayResult != null) {
            updateExpression("0.")
            return
        }
        // Avoid double decimal in the same number token
        val lastToken = current.split(Regex("[+\\-−×÷^%()]")).lastOrNull() ?: ""
        if (!lastToken.contains('.')) {
            val prefix = if (current.isEmpty() || current.last() in "+−×÷^%(") "0." else "."
            updateExpression(current + prefix)
        }
    }

    fun onParenthesis() {
        val current = _uiState.value.expression
        val openCount = current.count { it == '(' }
        val closeCount = current.count { it == ')' }

        val newExpr = if (openCount > closeCount && current.isNotEmpty() && current.last().isDigit()) {
            current + ")"
        } else if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
            current + "×("
        } else {
            current + "("
        }
        updateExpression(newExpr)
    }

    fun onFunction(func: String) {
        val current = _uiState.value.expression
        val isInv = _uiState.value.isInverse
        val actualFunc = when (func) {
            "sin" -> if (isInv) "asin(" else "sin("
            "cos" -> if (isInv) "acos(" else "cos("
            "tan" -> if (isInv) "atan(" else "tan("
            "ln" -> if (isInv) "exp(" else "ln("
            "log" -> if (isInv) "10^(" else "log("
            "sqrt" -> if (isInv) "^2" else "√("
            "inv" -> "1/("
            "abs" -> "abs("
            "fact" -> "!"
            "power" -> "^"
            "pi" -> "π"
            "e" -> "e"
            else -> "$func("
        }

        val newExpr = if (actualFunc == "^" || actualFunc == "^2" || actualFunc == "!") {
            current + actualFunc
        } else if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
            current + "×" + actualFunc
        } else {
            current + actualFunc
        }
        updateExpression(newExpr)
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                liveResult = null,
                displayResult = null,
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        val current = _uiState.value.expression
        if (current.isNotEmpty()) {
            // Check if removing multi-char token like 'asin(', 'sin(', 'sqrt('
            val patterns = listOf("asin(", "acos(", "atan(", "sqrt(", "log(", "exp(", "abs(", "sin(", "cos(", "tan(", "ln(", "10^(")
            var removed = false
            for (p in patterns) {
                if (current.endsWith(p)) {
                    updateExpression(current.dropLast(p.length))
                    removed = true
                    break
                }
            }
            if (!removed) {
                updateExpression(current.dropLast(1))
            }
        }
    }

    fun onEquals() {
        val expr = _uiState.value.expression.trim()
        if (expr.isEmpty()) return

        when (val eval = evaluator.evaluate(expr, _uiState.value.angleMode)) {
            is CalculatorEvaluator.EvalResult.Success -> {
                val formatted = eval.formatted
                _uiState.update {
                    it.copy(
                        displayResult = formatted,
                        liveResult = null,
                        errorMessage = null
                    )
                }

                // Save to Room database history
                val calcType = if (_uiState.value.isScientific) "scientific" else "basic"
                viewModelScope.launch {
                    historyRepository.addCalculation(
                        expression = expr,
                        result = formatted,
                        category = calcType
                    )
                }
            }
            is CalculatorEvaluator.EvalResult.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessage = eval.message,
                        liveResult = null
                    )
                }
            }
        }
    }

    fun toggleScientific() {
        _uiState.update { it.copy(isScientific = !it.isScientific) }
    }

    fun toggleAngleMode() {
        val newMode = if (_uiState.value.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        _uiState.update { it.copy(angleMode = newMode) }
        reEvaluate(newMode)
    }

    fun toggleInverse() {
        _uiState.update { it.copy(isInverse = !it.isInverse) }
    }

    fun setHistoryOpen(open: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = open) }
    }

    fun reuseResult(result: String) {
        updateExpression(result)
        _uiState.update { it.copy(isHistoryOpen = false, displayResult = null) }
    }

    fun reuseExpression(expression: String) {
        updateExpression(expression)
        _uiState.update { it.copy(isHistoryOpen = false, displayResult = null) }
    }

    fun deleteHistoryItem(entity: CalculationEntity) {
        viewModelScope.launch {
            historyRepository.deleteCalculation(entity)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
        }
    }

    // Memory operations
    fun memoryAdd() {
        val currentVal = getCurrentNumericValue() ?: return
        val newMem = _uiState.value.memoryValue + currentVal
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = true) }
    }

    fun memorySubtract() {
        val currentVal = getCurrentNumericValue() ?: return
        val newMem = _uiState.value.memoryValue - currentVal
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = true) }
    }

    fun memoryRecall() {
        if (_uiState.value.hasMemory) {
            val memStr = _uiState.value.memoryValue.toString().removeSuffix(".0")
            onDigit(memStr)
        }
    }

    fun memoryClear() {
        _uiState.update { it.copy(memoryValue = 0.0, hasMemory = false) }
    }

    private fun getCurrentNumericValue(): Double? {
        val display = _uiState.value.displayResult
        if (display != null) return display.toDoubleOrNull()
        val expr = _uiState.value.expression
        return expr.toDoubleOrNull() ?: run {
            val eval = evaluator.evaluate(expr, _uiState.value.angleMode)
            if (eval is CalculatorEvaluator.EvalResult.Success) eval.value else null
        }
    }

    private fun updateExpression(newExpr: String) {
        _uiState.update {
            it.copy(
                expression = newExpr,
                displayResult = null,
                errorMessage = null
            )
        }
        reEvaluate(_uiState.value.angleMode)
    }

    private fun reEvaluate(angleMode: AngleMode) {
        val expr = _uiState.value.expression.trim()
        if (expr.isEmpty()) {
            _uiState.update { it.copy(liveResult = null) }
            return
        }

        // Try evaluating live
        when (val eval = evaluator.evaluate(expr, angleMode)) {
            is CalculatorEvaluator.EvalResult.Success -> {
                // Only show live preview if it contains operators or functions
                if (expr.any { it in "+−×÷^%!scl" }) {
                    _uiState.update { it.copy(liveResult = eval.formatted) }
                } else {
                    _uiState.update { it.copy(liveResult = null) }
                }
            }
            is CalculatorEvaluator.EvalResult.Error -> {
                _uiState.update { it.copy(liveResult = null) }
            }
        }
    }
}
