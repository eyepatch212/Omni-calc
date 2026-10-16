package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calculator.AngleMode
import com.example.calculator.CalculatorEvaluator
import com.example.converters.FinancialAndScienceConverters
import com.example.converters.UnitConverters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OmniCalc", appName)
  }

  @Test
  fun `calculator basic arithmetic`() {
    val evaluator = CalculatorEvaluator()
    val res1 = evaluator.evaluate("2+3×4")
    assertTrue(res1 is CalculatorEvaluator.EvalResult.Success)
    assertEquals("14", (res1 as CalculatorEvaluator.EvalResult.Success).formatted)
  }

  @Test
  fun `calculator scientific functions`() {
    val evaluator = CalculatorEvaluator()

    // Factorial
    val resFact = evaluator.evaluate("5!")
    assertTrue(resFact is CalculatorEvaluator.EvalResult.Success)
    assertEquals("120", (resFact as CalculatorEvaluator.EvalResult.Success).formatted)

    // Trigonometry in DEG mode
    val resSin = evaluator.evaluate("sin(90)", AngleMode.DEG)
    assertTrue(resSin is CalculatorEvaluator.EvalResult.Success)
    assertEquals("1", (resSin as CalculatorEvaluator.EvalResult.Success).formatted)

    // Logarithms
    val resLog = evaluator.evaluate("log(100)")
    assertTrue(resLog is CalculatorEvaluator.EvalResult.Success)
    assertEquals("2", (resLog as CalculatorEvaluator.EvalResult.Success).formatted)

    // Natural Log of e
    val resLn = evaluator.evaluate("ln(e)")
    assertTrue(resLn is CalculatorEvaluator.EvalResult.Success)
    assertEquals("1", (resLn as CalculatorEvaluator.EvalResult.Success).formatted)

    // Power
    val resPow = evaluator.evaluate("2^8")
    assertTrue(resPow is CalculatorEvaluator.EvalResult.Success)
    assertEquals("256", (resPow as CalculatorEvaluator.EvalResult.Success).formatted)
  }

  @Test
  fun `molarity calculation`() {
    // 58.44g of NaCl (MW 58.44) in 1 L = 1.0 M
    val molarity = FinancialAndScienceConverters.calculateMolarity(58.44, 58.44, 1.0)
    assertEquals(1.0, molarity, 0.001)
  }

  @Test
  fun `unit conversion length`() {
    val meters = UnitConverters.lengthUnits.first { it.id == "m" }
    val feet = UnitConverters.lengthUnits.first { it.id == "ft" }
    val converted = UnitConverters.convertLinear(1.0, meters, feet)
    assertEquals(3.28084, converted, 0.001)
  }
}
