package com.example.ui.calculator

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.AngleMode

enum class KeypadButtonType {
    DIGIT,
    OPERATOR,
    EQUALS,
    CLEAR,
    FUNCTION,
    BACKSPACE
}

@Composable
fun CalculatorKeypad(
    isScientific: Boolean,
    angleMode: AngleMode,
    isInverse: Boolean,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onParenthesis: () -> Unit,
    onFunction: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    onToggleAngleMode: () -> Unit,
    onToggleInverse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val triggerHaptic = {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Scientific Expansion Section
        AnimatedVisibility(
            visible = isScientific,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top controls: Angle Mode (DEG/RAD) & Inverse (INV) toggles + sin, cos, tan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalcKey(
                        text = if (angleMode == AngleMode.DEG) "DEG" else "RAD",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        isActive = true,
                        onClick = {
                            triggerHaptic()
                            onToggleAngleMode()
                        }
                    )
                    CalcKey(
                        text = "INV",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        isActive = isInverse,
                        onClick = {
                            triggerHaptic()
                            onToggleInverse()
                        }
                    )
                    CalcKey(
                        text = if (isInverse) "sin⁻¹" else "sin",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("sin")
                        }
                    )
                    CalcKey(
                        text = if (isInverse) "cos⁻¹" else "cos",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("cos")
                        }
                    )
                    CalcKey(
                        text = if (isInverse) "tan⁻¹" else "tan",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("tan")
                        }
                    )
                }

                // Row 2: ln (or e^x), log (or 10^x), 1/x, e, π
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalcKey(
                        text = if (isInverse) "eˣ" else "ln",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("ln")
                        }
                    )
                    CalcKey(
                        text = if (isInverse) "10ˣ" else "log",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("log")
                        }
                    )
                    CalcKey(
                        text = "1/x",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("inv")
                        }
                    )
                    CalcKey(
                        text = "e",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("e")
                        }
                    )
                    CalcKey(
                        text = "π",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("pi")
                        }
                    )
                }

                // Row 3: sqrt (or x^2), power x^y, x!, |x|, ( )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalcKey(
                        text = if (isInverse) "x²" else "√",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("sqrt")
                        }
                    )
                    CalcKey(
                        text = "xʸ",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("power")
                        }
                    )
                    CalcKey(
                        text = "x!",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("fact")
                        }
                    )
                    CalcKey(
                        text = "|x|",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onFunction("abs")
                        }
                    )
                    CalcKey(
                        text = "( )",
                        type = KeypadButtonType.FUNCTION,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            triggerHaptic()
                            onParenthesis()
                        }
                    )
                }
            }
        }

        // Standard Keypad Rows (4 columns)
        // Row 1: AC, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcKey(
                text = "AC",
                type = KeypadButtonType.CLEAR,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onClear()
                }
            )
            CalcKey(
                text = "( )",
                type = KeypadButtonType.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onParenthesis()
                }
            )
            CalcKey(
                text = "%",
                type = KeypadButtonType.FUNCTION,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onOperator("%")
                }
            )
            CalcKey(
                text = "÷",
                type = KeypadButtonType.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onOperator("÷")
                }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcKey(
                text = "7",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("7")
                }
            )
            CalcKey(
                text = "8",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("8")
                }
            )
            CalcKey(
                text = "9",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("9")
                }
            )
            CalcKey(
                text = "×",
                type = KeypadButtonType.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onOperator("×")
                }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcKey(
                text = "4",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("4")
                }
            )
            CalcKey(
                text = "5",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("5")
                }
            )
            CalcKey(
                text = "6",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("6")
                }
            )
            CalcKey(
                text = "−",
                type = KeypadButtonType.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onOperator("−")
                }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcKey(
                text = "1",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("1")
                }
            )
            CalcKey(
                text = "2",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("2")
                }
            )
            CalcKey(
                text = "3",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("3")
                }
            )
            CalcKey(
                text = "+",
                type = KeypadButtonType.OPERATOR,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onOperator("+")
                }
            )
        }

        // Row 5: 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcKey(
                text = "0",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDigit("0")
                }
            )
            CalcKey(
                text = ".",
                type = KeypadButtonType.DIGIT,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onDecimal()
                }
            )
            CalcKey(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        modifier = Modifier.size(22.dp)
                    )
                },
                type = KeypadButtonType.BACKSPACE,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onBackspace()
                }
            )
            CalcKey(
                text = "=",
                type = KeypadButtonType.EQUALS,
                modifier = Modifier.weight(1f),
                onClick = {
                    triggerHaptic()
                    onEquals()
                }
            )
        }
    }
}

@Composable
fun CalcKey(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: (@Composable () -> Unit)? = null,
    type: KeypadButtonType,
    isActive: Boolean = false,
    height: Dp = 56.dp,
    onClick: () -> Unit
) {
    val containerColor = when (type) {
        KeypadButtonType.EQUALS -> MaterialTheme.colorScheme.primary
        KeypadButtonType.OPERATOR -> MaterialTheme.colorScheme.secondaryContainer
        KeypadButtonType.CLEAR -> MaterialTheme.colorScheme.errorContainer
        KeypadButtonType.FUNCTION -> if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        KeypadButtonType.BACKSPACE -> MaterialTheme.colorScheme.surfaceVariant
        KeypadButtonType.DIGIT -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val contentColor = when (type) {
        KeypadButtonType.EQUALS -> MaterialTheme.colorScheme.onPrimary
        KeypadButtonType.OPERATOR -> MaterialTheme.colorScheme.onSecondaryContainer
        KeypadButtonType.CLEAR -> MaterialTheme.colorScheme.onErrorContainer
        KeypadButtonType.FUNCTION -> if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        KeypadButtonType.BACKSPACE -> MaterialTheme.colorScheme.onSurfaceVariant
        KeypadButtonType.DIGIT -> MaterialTheme.colorScheme.onSurface
    }

    val shape = RoundedCornerShape(24.dp)

    Surface(
        modifier = modifier
            .height(height)
            .clip(shape)
            .clickable(onClick = onClick)
            .testTag("key_${text ?: "icon"}"),
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        tonalElevation = if (type == KeypadButtonType.DIGIT) 2.dp else 4.dp
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            } else if (text != null) {
                Text(
                    text = text,
                    fontSize = if (text.length > 3) 14.sp else if (text.length > 2) 16.sp else 22.sp,
                    fontWeight = if (type == KeypadButtonType.EQUALS || type == KeypadButtonType.OPERATOR) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
