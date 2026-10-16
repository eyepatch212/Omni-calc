package com.example.ui.calculator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // Auto-scroll expression to end when updated so newly typed numbers are always visible
    LaunchedEffect(uiState.expression) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Mode toggle chip + Compact Memory Buttons + History
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
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
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (uiState.isScientific) Icons.Default.Science else Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("calculator_mode_chip")
                )

                // Compact Memory Buttons & History Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { viewModel.memoryClear() },
                        enabled = uiState.hasMemory,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("MC", fontSize = 12.sp, fontWeight = if (uiState.hasMemory) FontWeight.Bold else FontWeight.Normal)
                    }
                    TextButton(
                        onClick = { viewModel.memoryRecall() },
                        enabled = uiState.hasMemory,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("MR", fontSize = 12.sp, fontWeight = if (uiState.hasMemory) FontWeight.Bold else FontWeight.Normal)
                    }
                    TextButton(
                        onClick = { viewModel.memoryAdd() },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("M+", fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = { viewModel.memorySubtract() },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("M−", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // History Icon with Badge
                    IconButton(
                        onClick = { viewModel.setHistoryOpen(true) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.recentHistory.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(uiState.recentHistory.size.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Display Screen Container Card
            // Uses guaranteed minimum height and flexible weight so it never collapses or clips numbers
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .heightIn(min = 100.dp)
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    // Adaptive font size so expressions are clearly readable
                    val exprFontSize = when {
                        uiState.expression.length > 24 -> 22.sp
                        uiState.expression.length > 16 -> 26.sp
                        uiState.expression.length > 10 -> 32.sp
                        else -> 38.sp
                    }

                    // Typed Expression Text (Horizontally scrollable, always anchored to the end)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = if (uiState.expression.isEmpty() && uiState.displayResult == null) "0" else uiState.expression,
                            fontSize = exprFontSize,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            modifier = Modifier.testTag("calculator_expression")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Live Pending Result Preview or Final Result
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("calculator_error")
                        )
                    } else if (uiState.displayResult != null) {
                        Text(
                            text = "= ${uiState.displayResult}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("calculator_final_result")
                        )
                    } else if (uiState.liveResult != null) {
                        Text(
                            text = "= ${uiState.liveResult}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("calculator_live_result")
                        )
                    } else {
                        // Keep subtle space so layout doesn't jump
                        Spacer(modifier = Modifier.height(24.dp))
                    }
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
                modifier = Modifier.padding(bottom = 2.dp)
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
