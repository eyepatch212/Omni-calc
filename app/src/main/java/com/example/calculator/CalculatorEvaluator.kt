package com.example.calculator

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

enum class AngleMode {
    DEG, RAD
}

class CalculatorEvaluator {

    sealed class EvalResult {
        data class Success(val value: Double, val formatted: String) : EvalResult()
        data class Error(val message: String) : EvalResult()
    }

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG): EvalResult {
        val sanitized = sanitize(expression)
        if (sanitized.isBlank()) {
            return EvalResult.Error("Empty expression")
        }

        return try {
            val parser = Parser(sanitized, angleMode)
            val result = parser.parse()
            if (result.isNaN()) {
                EvalResult.Error("Result is undefined")
            } else if (result.isInfinite()) {
                EvalResult.Error("Cannot divide by 0")
            } else {
                EvalResult.Success(result, formatResult(result))
            }
        } catch (e: ArithmeticException) {
            EvalResult.Error(e.message ?: "Math error")
        } catch (e: IllegalArgumentException) {
            EvalResult.Error(e.message ?: "Invalid format")
        } catch (e: Exception) {
            EvalResult.Error("Invalid expression")
        }
    }

    private fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .trim()
    }

    private fun formatResult(value: Double): String {
        if (value.isInfinite() || value.isNaN()) return value.toString()

        // Very large or very small numbers
        val absVal = kotlin.math.abs(value)
        if (absVal != 0.0 && (absVal >= 1e12 || absVal < 1e-7)) {
            val symbols = DecimalFormatSymbols(Locale.US)
            val sciFormat = DecimalFormat("0.######E0", symbols)
            return sciFormat.format(value)
        }

        // Format to standard decimal with up to 10 decimals, removing trailing zeros
        val bd = BigDecimal(value.toString())
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()

        return bd.toPlainString()
    }

    private class Parser(private val input: String, private val angleMode: AngleMode) {
        private var pos = 0
        private val length = input.length

        fun parse(): Double {
            val value = parseExpression()
            skipWhitespace()
            if (pos < length) {
                throw IllegalArgumentException("Unexpected symbol: '${input[pos]}'")
            }
            return value
        }

        // Expression: Term (('+' | '-') Term)*
        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                skipWhitespace()
                if (consume('+')) {
                    x += parseTerm()
                } else if (consume('-')) {
                    x -= parseTerm()
                } else {
                    return x
                }
            }
        }

        // Term: Factor (('*' | '/' | '%') Factor)*
        private fun parseTerm(): Double {
            var x = parsePower()
            while (true) {
                skipWhitespace()
                if (consume('*')) {
                    x *= parsePower()
                } else if (consume('/')) {
                    val divisor = parsePower()
                    if (divisor == 0.0) throw ArithmeticException("Cannot divide by 0")
                    x /= divisor
                } else if (consume('%')) {
                    val divisor = parsePower()
                    if (divisor == 0.0) throw ArithmeticException("Cannot divide by 0")
                    x %= divisor
                } else {
                    return x
                }
            }
        }

        // Power: Factor ('^' Factor)? (right-associative)
        private fun parsePower(): Double {
            val base = parseFactorial()
            skipWhitespace()
            return if (consume('^')) {
                val exponent = parsePower()
                base.pow(exponent)
            } else {
                base
            }
        }

        // Factorial: Factor '!'?
        private fun parseFactorial(): Double {
            var x = parseFactor()
            skipWhitespace()
            while (consume('!')) {
                if (x < 0 || x > 170 || x % 1.0 != 0.0) {
                    throw ArithmeticException("Factorial only defined for non-negative integers <= 170")
                }
                x = factorial(x.toLong())
            }
            return x
        }

        private fun factorial(n: Long): Double {
            var res = 1.0
            for (i in 2..n) {
                res *= i
            }
            return res
        }

        // Factor: Unary sign, numbers, parentheses, or functions
        private fun parseFactor(): Double {
            skipWhitespace()
            if (consume('+')) return parseFactor()
            if (consume('-')) return -parseFactor()

            val startPos = pos
            if (consume('(')) {
                val x = parseExpression()
                skipWhitespace()
                if (!consume(')')) throw IllegalArgumentException("Missing closing parenthesis")
                return x
            }

            // Check for numbers or constants
            if (isDigit(currentChar()) || currentChar() == '.') {
                return parseNumber()
            }

            // Identifiers / Functions
            if (isLetter(currentChar())) {
                val name = parseIdentifier()
                skipWhitespace()
                return when (name.lowercase()) {
                    "pi" -> Math.PI
                    "e" -> Math.E
                    "sin" -> {
                        val arg = parseFunctionArg()
                        val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                        val res = sin(rad)
                        if (kotlin.math.abs(res) < 1e-15) 0.0 else res
                    }
                    "cos" -> {
                        val arg = parseFunctionArg()
                        val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                        val res = cos(rad)
                        if (kotlin.math.abs(res) < 1e-15) 0.0 else res
                    }
                    "tan" -> {
                        val arg = parseFunctionArg()
                        val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                        if (cos(rad) == 0.0 || kotlin.math.abs(cos(rad)) < 1e-15) {
                            throw ArithmeticException("Tangent undefined at this angle")
                        }
                        tan(rad)
                    }
                    "asin" -> {
                        val arg = parseFunctionArg()
                        if (arg < -1.0 || arg > 1.0) throw ArithmeticException("asin domain error: [-1, 1]")
                        val res = asin(arg)
                        if (angleMode == AngleMode.DEG) Math.toDegrees(res) else res
                    }
                    "acos" -> {
                        val arg = parseFunctionArg()
                        if (arg < -1.0 || arg > 1.0) throw ArithmeticException("acos domain error: [-1, 1]")
                        val res = acos(arg)
                        if (angleMode == AngleMode.DEG) Math.toDegrees(res) else res
                    }
                    "atan" -> {
                        val arg = parseFunctionArg()
                        val res = atan(arg)
                        if (angleMode == AngleMode.DEG) Math.toDegrees(res) else res
                    }
                    "ln" -> {
                        val arg = parseFunctionArg()
                        if (arg <= 0.0) throw ArithmeticException("ln requires positive number")
                        ln(arg)
                    }
                    "log", "log10" -> {
                        val arg = parseFunctionArg()
                        if (arg <= 0.0) throw ArithmeticException("log requires positive number")
                        log10(arg)
                    }
                    "sqrt" -> {
                        val arg = parseFunctionArg()
                        if (arg < 0.0) throw ArithmeticException("Cannot take sqrt of negative number")
                        sqrt(arg)
                    }
                    "cbrt" -> {
                        val arg = parseFunctionArg()
                        Math.cbrt(arg)
                    }
                    "exp" -> {
                        val arg = parseFunctionArg()
                        exp(arg)
                    }
                    "abs" -> {
                        val arg = parseFunctionArg()
                        kotlin.math.abs(arg)
                    }
                    else -> throw IllegalArgumentException("Unknown function '$name'")
                }
            }

            throw IllegalArgumentException("Unexpected character '${currentChar()}' at position $startPos")
        }

        private fun parseFunctionArg(): Double {
            skipWhitespace()
            return if (consume('(')) {
                val arg = parseExpression()
                skipWhitespace()
                if (!consume(')')) throw IllegalArgumentException("Missing closing ')' in function")
                arg
            } else {
                parseFactor()
            }
        }

        private fun parseNumber(): Double {
            val start = pos
            while (pos < length && (isDigit(input[pos]) || input[pos] == '.')) {
                pos++
            }
            val numStr = input.substring(start, pos)
            return try {
                numStr.toDouble()
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid number '$numStr'")
            }
        }

        private fun parseIdentifier(): String {
            val start = pos
            while (pos < length && isLetter(input[pos])) {
                pos++
            }
            return input.substring(start, pos)
        }

        private fun currentChar(): Char {
            return if (pos < length) input[pos] else '\u0000'
        }

        private fun consume(expected: Char): Boolean {
            skipWhitespace()
            if (pos < length && input[pos] == expected) {
                pos++
                return true
            }
            return false
        }

        private fun skipWhitespace() {
            while (pos < length && input[pos].isWhitespace()) {
                pos++
            }
        }

        private fun isDigit(c: Char) = c in '0'..'9'
        private fun isLetter(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z')
    }
}
