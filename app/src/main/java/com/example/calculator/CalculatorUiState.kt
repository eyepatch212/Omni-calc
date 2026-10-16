package com.example.calculator

import com.example.data.CalculationEntity

data class CalculatorUiState(
    val expression: String = "",
    val liveResult: String? = null,
    val displayResult: String? = null,
    val errorMessage: String? = null,
    val isScientific: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEG,
    val isInverse: Boolean = false, // When true: sin->asin, cos->acos, tan->atan, ln->e^x, log->10^x, sqrt->x^2
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val recentHistory: List<CalculationEntity> = emptyList()
)
