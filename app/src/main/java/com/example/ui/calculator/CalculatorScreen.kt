package com.example.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calculator.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Auto-scroll expression to end when updated
    LaunchedEffect(uiState.expression) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Mode toggles and History
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scientific vs Basic Toggle Chip
                FilterChip(
                    selected = uiState.isScientific,
                    onClick = { viewModel.toggleScientific() },
                    label = {
                        Text(
                            text = if (uiState.isScientific) "Scientific" else "Basic",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (uiState.isScientific) Icons.Default.Science else Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("calculator_mode_chip")
                )

                // Memory indicators and History
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Memory indicators
                    if (uiState.hasMemory) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "M",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    // History Icon with Badge
                    IconButton(
                        onClick = { viewModel.setHistoryOpen(true) },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.recentHistory.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(uiState.recentHistory.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Display Screen Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                // Memory controls bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = { viewModel.memoryClear() },
                        enabled = uiState.hasMemory
                    ) {
                        Text("MC", style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(
                        onClick = { viewModel.memoryRecall() },
                        enabled = uiState.hasMemory
                    ) {
                        Text("MR", style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(onClick = { viewModel.memoryAdd() }) {
                        Text("M+", style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(onClick = { viewModel.memorySubtract() }) {
                        Text("M−", style = MaterialTheme.typography.labelMedium)
                    }
                }

                // Current Expression (scrollable horizontally)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (uiState.expression.isEmpty() && uiState.displayResult == null) "0" else uiState.expression,
                        fontSize = if (uiState.expression.length > 15) 32.sp else 44.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("calculator_expression")
                    )
                }

                // Live Pending Result Preview or Final Result
                Spacer(modifier = Modifier.height(6.dp))

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("calculator_error")
                    )
                } else if (uiState.displayResult != null) {
                    Text(
                        text = "= ${uiState.displayResult}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("calculator_final_result")
                    )
                } else if (uiState.liveResult != null) {
                    Text(
                        text = "= ${uiState.liveResult}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("calculator_live_result")
                    )
                } else {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // Keypad
            CalculatorKeypad(
                isScientific = uiState.isScientific,
                angleMode = uiState.angleMode,
                isInverse = uiState.isInverse,
                onDigit = viewModel::onDigit,
                onOperator = viewModel::onOperator,
                onDecimal = viewModel::onDecimal,
                onParenthesis = viewModel::onParenthesis,
                onFunction = viewModel::onFunction,
                onClear = viewModel::onClear,
                onBackspace = viewModel::onBackspace,
                onEquals = viewModel::onEquals,
                onToggleAngleMode = viewModel::toggleAngleMode,
                onToggleInverse = viewModel::toggleInverse,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // History Modal Bottom Sheet
        if (uiState.isHistoryOpen) {
            HistorySheet(
                historyList = uiState.recentHistory,
                onDismiss = { viewModel.setHistoryOpen(false) },
                onReuseResult = viewModel::reuseResult,
                onReuseExpression = viewModel::reuseExpression,
                onDeleteSingle = viewModel::deleteHistoryItem,
                onClearAll = viewModel::clearHistory
            )
        }
    }
}
