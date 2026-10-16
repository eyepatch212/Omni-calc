package com.example.converters

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object FinancialAndScienceConverters {

    // ==========================================
    // 1. CURRENCY CONVERTER
    // ==========================================
    data class Currency(
        val code: String,
        val name: String,
        val symbol: String,
        val defaultRateToUsd: Double // 1 USD = X Currency
    )

    val defaultCurrencies = listOf(
        Currency("USD", "US Dollar", "$", 1.0),
        Currency("EUR", "Euro", "€", 0.92),
        Currency("GBP", "British Pound", "£", 0.79),
        Currency("JPY", "Japanese Yen", "¥", 154.50),
        Currency("CAD", "Canadian Dollar", "CA$", 1.37),
        Currency("AUD", "Australian Dollar", "A$", 1.52),
        Currency("CHF", "Swiss Franc", "CHF", 0.91),
        Currency("CNY", "Chinese Yuan", "¥", 7.24),
        Currency("INR", "Indian Rupee", "₹", 83.50),
        Currency("BRL", "Brazilian Real", "R$", 5.15),
        Currency("MXN", "Mexican Peso", "Mex$", 16.85),
        Currency("KRW", "South Korean Won", "₩", 1375.0),
        Currency("SGD", "Singapore Dollar", "S$", 1.35),
        Currency("NZD", "New Zealand Dollar", "NZ$", 1.66),
        Currency("HKD", "Hong Kong Dollar", "HK$", 7.82),
        Currency("SEK", "Swedish Krona", "kr", 10.82),
        Currency("NOK", "Norwegian Krone", "kr", 10.90),
        Currency("TRY", "Turkish Lira", "₺", 32.40),
        Currency("ZAR", "South African Rand", "R", 18.65),
        Currency("AED", "UAE Dirham", "AED", 3.67),
        Currency("SAR", "Saudi Riyal", "SAR", 3.75),
        Currency("THB", "Thai Baht", "฿", 36.80)
    )

    fun convertCurrency(amount: Double, fromRateToUsd: Double, toRateToUsd: Double): Double {
        val amountInUsd = amount / fromRateToUsd
        return amountInUsd * toRateToUsd
    }

    // ==========================================
    // 2. MOLARITY & CHEMISTRY
    // ==========================================
    data class ChemicalCompound(
        val formula: String,
        val name: String,
        val molarMass: Double // g/mol
    )

    val commonCompounds = listOf(
        ChemicalCompound("NaCl", "Sodium Chloride (Salt)", 58.44),
        ChemicalCompound("C6H12O6", "Glucose", 180.16),
        ChemicalCompound("NaOH", "Sodium Hydroxide", 40.00),
        ChemicalCompound("HCl", "Hydrochloric Acid", 36.46),
        ChemicalCompound("H2SO4", "Sulfuric Acid", 98.08),
        ChemicalCompound("C2H5OH", "Ethanol", 46.07),
        ChemicalCompound("CH4N2O", "Urea", 60.06),
        ChemicalCompound("CuSO4", "Copper(II) Sulfate", 159.61),
        ChemicalCompound("KCl", "Potassium Chloride", 74.55),
        ChemicalCompound("NaHCO3", "Sodium Bicarbonate", 84.01),
        ChemicalCompound("CaCO3", "Calcium Carbonate", 100.09),
        ChemicalCompound("H2O", "Water", 18.015)
    )

    // Mode A: Calculate Molarity from mass (g), molarMass (g/mol), and volume (mL or L)
    fun calculateMolarity(massGrams: Double, molarMassGPerMol: Double, volumeLiters: Double): Double {
        if (molarMassGPerMol <= 0 || volumeLiters <= 0) return 0.0
        val moles = massGrams / molarMassGPerMol
        return moles / volumeLiters
    }

    // Mode B: Calculate required mass (g) for desired molarity (mol/L) and volume (L)
    fun calculateRequiredMass(molarity: Double, molarMassGPerMol: Double, volumeLiters: Double): Double {
        val moles = molarity * volumeLiters
        return moles * molarMassGPerMol
    }

    // Mode C: Dilution C1 * V1 = C2 * V2 -> solve for missing variable (pass null for target)
    fun solveDilution(c1: Double?, v1: Double?, c2: Double?, v2: Double?): Double {
        return when {
            c1 == null && v1 != null && c2 != null && v2 != null && v1 > 0 -> (c2 * v2) / v1
            v1 == null && c1 != null && c2 != null && v2 != null && c1 > 0 -> (c2 * v2) / c1
            c2 == null && c1 != null && v1 != null && v2 != null && v2 > 0 -> (c1 * v1) / v2
            v2 == null && c1 != null && v1 != null && c2 != null && c2 > 0 -> (c1 * v1) / c2
            else -> 0.0
        }
    }

    // ==========================================
    // 3. INCOME TAX
    // ==========================================
    enum class TaxFilingStatus(val label: String, val standardDeduction: Double) {
        SINGLE("Single", 14600.0),
        MARRIED_JOINT("Married Filing Jointly", 29200.0),
        HEAD_OF_HOUSEHOLD("Head of Household", 21900.0)
    }

    data class TaxBracket(val rate: Double, val singleCap: Double, val marriedCap: Double, val hohCap: Double)

    private val federalBrackets = listOf(
        TaxBracket(0.10, 11600.0, 23200.0, 16550.0),
        TaxBracket(0.12, 47150.0, 94300.0, 63100.0),
        TaxBracket(0.22, 100525.0, 201050.0, 100500.0),
        TaxBracket(0.24, 191950.0, 383900.0, 191950.0),
        TaxBracket(0.32, 243725.0, 487450.0, 243700.0),
        TaxBracket(0.35, 609350.0, 731200.0, 609350.0),
        TaxBracket(0.37, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)
    )

    data class TaxBracketBreakdown(
        val ratePercent: Double,
        val bracketRange: String,
        val amountInBracket: Double,
        val taxInBracket: Double
    )

    data class TaxResult(
        val grossIncome: Double,
        val deduction: Double,
        val taxableIncome: Double,
        val totalTax: Double,
        val effectiveRatePercent: Double,
        val marginalRatePercent: Double,
        val netTakeHomeAnnual: Double,
        val netTakeHomeMonthly: Double,
        val brackets: List<TaxBracketBreakdown>
    )

    fun calculateIncomeTax(
        grossIncome: Double,
        status: TaxFilingStatus,
        customDeduction: Double? = null
    ): TaxResult {
        val deduction = customDeduction ?: status.standardDeduction
        val taxableIncome = max(0.0, grossIncome - deduction)

        var remainingTaxable = taxableIncome
        var lowerThreshold = 0.0
        var totalTax = 0.0
        var marginalRate = 0.0
        val breakdowns = mutableListOf<TaxBracketBreakdown>()

        for (bracket in federalBrackets) {
            val upperThreshold = when (status) {
                TaxFilingStatus.SINGLE -> bracket.singleCap
                TaxFilingStatus.MARRIED_JOINT -> bracket.marriedCap
                TaxFilingStatus.HEAD_OF_HOUSEHOLD -> bracket.hohCap
            }

            val bracketWidth = upperThreshold - lowerThreshold
            if (remainingTaxable > 0) {
                val taxableInThisBracket = min(remainingTaxable, bracketWidth)
                val taxForThisBracket = taxableInThisBracket * bracket.rate
                totalTax += taxForThisBracket
                marginalRate = bracket.rate * 100

                val rangeLabel = if (upperThreshold == Double.MAX_VALUE) {
                    "$${formatMoney(lowerThreshold)}+"
                } else {
                    "$${formatMoney(lowerThreshold)} - $${formatMoney(upperThreshold)}"
                }

                breakdowns.add(
                    TaxBracketBreakdown(
                        ratePercent = bracket.rate * 100,
                        bracketRange = rangeLabel,
                        amountInBracket = taxableInThisBracket,
                        taxInBracket = taxForThisBracket
                    )
                )

                remainingTaxable -= taxableInThisBracket
            }

            lowerThreshold = upperThreshold
            if (remainingTaxable <= 0 && lowerThreshold >= taxableIncome) break
        }

        val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100 else 0.0
        val netAnnual = grossIncome - totalTax
        val netMonthly = netAnnual / 12.0

        return TaxResult(
            grossIncome = grossIncome,
            deduction = deduction,
            taxableIncome = taxableIncome,
            totalTax = totalTax,
            effectiveRatePercent = effectiveRate,
            marginalRatePercent = marginalRate,
            netTakeHomeAnnual = netAnnual,
            netTakeHomeMonthly = netMonthly,
            brackets = breakdowns
        )
    }

    // ==========================================
    // 4. MORTGAGE & LOAN
    // ==========================================
    data class AmortizationYear(
        val year: Int,
        val interestPaid: Double,
        val principalPaid: Double,
        val remainingBalance: Double
    )

    data class MortgageResult(
        val loanAmount: Double,
        val monthlyPrincipalAndInterest: Double,
        val monthlyPropertyTax: Double,
        val monthlyInsurance: Double,
        val totalMonthlyPayment: Double,
        val totalInterest: Double,
        val totalCost: Double,
        val amortizationSchedule: List<AmortizationYear>
    )

    fun calculateMortgage(
        homePrice: Double,
        downPayment: Double,
        annualInterestRatePercent: Double,
        loanTermYears: Int,
        annualPropertyTax: Double = 0.0,
        annualInsurance: Double = 0.0
    ): MortgageResult {
        val loanAmount = max(0.0, homePrice - downPayment)
        if (loanAmount <= 0.0 || loanTermYears <= 0) {
            return MortgageResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
        }

        val monthlyRate = (annualInterestRatePercent / 100.0) / 12.0
        val totalMonths = loanTermYears * 12

        val monthlyPI = if (monthlyRate > 0) {
            val factor = (1.0 + monthlyRate).pow(totalMonths.toDouble())
            loanAmount * (monthlyRate * factor) / (factor - 1.0)
        } else {
            loanAmount / totalMonths
        }

        val monthlyTax = annualPropertyTax / 12.0
        val monthlyIns = annualInsurance / 12.0
        val totalMonthly = monthlyPI + monthlyTax + monthlyIns

        // Calculate amortization
        var balance = loanAmount
        var totalInterestPaid = 0.0
        val schedule = mutableListOf<AmortizationYear>()

        for (year in 1..loanTermYears) {
            var yearInterest = 0.0
            var yearPrincipal = 0.0
            for (m in 1..12) {
                val interestMonth = balance * monthlyRate
                val principalMonth = min(balance, monthlyPI - interestMonth)
                yearInterest += interestMonth
                yearPrincipal += principalMonth
                balance = max(0.0, balance - principalMonth)
            }
            totalInterestPaid += yearInterest
            schedule.add(
                AmortizationYear(
                    year = year,
                    interestPaid = yearInterest,
                    principalPaid = yearPrincipal,
                    remainingBalance = balance
                )
            )
        }

        val totalCost = (monthlyPI * totalMonths) + downPayment + (annualPropertyTax * loanTermYears) + (annualInsurance * loanTermYears)

        return MortgageResult(
            loanAmount = loanAmount,
            monthlyPrincipalAndInterest = monthlyPI,
            monthlyPropertyTax = monthlyTax,
            monthlyInsurance = monthlyIns,
            totalMonthlyPayment = totalMonthly,
            totalInterest = totalInterestPaid,
            totalCost = totalCost,
            amortizationSchedule = schedule
        )
    }

    // ==========================================
    // 5. BMI CONVERTER
    // ==========================================
    enum class BmiCategory(val label: String, val colorHex: Long) {
        UNDERWEIGHT("Underweight (< 18.5)", 0xFF3B82F6),
        NORMAL("Normal weight (18.5 - 24.9)", 0xFF10B981),
        OVERWEIGHT("Overweight (25 - 29.9)", 0xFFF59E0B),
        OBESE_CLASS_1("Obese Class I (30 - 34.9)", 0xFFEF4444),
        OBESE_CLASS_2("Obese Class II (35 - 39.9)", 0xFFDC2626),
        OBESE_CLASS_3("Obese Class III (≥ 40)", 0xFF991B1B)
    }

    data class BmiResult(
        val bmi: Double,
        val category: BmiCategory,
        val healthyWeightMinKg: Double,
        val healthyWeightMaxKg: Double,
        val healthyWeightMinLb: Double,
        val healthyWeightMaxLb: Double,
        val primeDifferenceKg: Double
    )

    fun calculateBmiMetric(weightKg: Double, heightCm: Double): BmiResult? {
        if (weightKg <= 0 || heightCm <= 0) return null
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        val minHealthyKg = 18.5 * (heightM * heightM)
        val maxHealthyKg = 24.9 * (heightM * heightM)

        val cat = when {
            bmi < 18.5 -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            bmi < 35.0 -> BmiCategory.OBESE_CLASS_1
            bmi < 40.0 -> BmiCategory.OBESE_CLASS_2
            else -> BmiCategory.OBESE_CLASS_3
        }

        val diff = if (bmi < 18.5) {
            minHealthyKg - weightKg // Need to gain
        } else if (bmi > 24.9) {
            weightKg - maxHealthyKg // Need to lose
        } else {
            0.0
        }

        return BmiResult(
            bmi = bmi,
            category = cat,
            healthyWeightMinKg = minHealthyKg,
            healthyWeightMaxKg = maxHealthyKg,
            healthyWeightMinLb = minHealthyKg * 2.20462,
            healthyWeightMaxLb = maxHealthyKg * 2.20462,
            primeDifferenceKg = diff
        )
    }

    // ==========================================
    // 6. NUMERAL SYSTEM
    // ==========================================
    data class NumeralRepresentation(
        val decimal: String,
        val hex: String,
        val binary: String,
        val octal: String,
        val formattedBinary: String
    )

    fun convertNumeral(input: String, fromBase: Int): NumeralRepresentation? {
        val clean = input.trim().replace(" ", "").replace("_", "")
        if (clean.isEmpty()) return null

        val value: Long = try {
            when (fromBase) {
                10 -> clean.toLong()
                16 -> clean.toLong(16)
                2 -> clean.toLong(2)
                8 -> clean.toLong(8)
                else -> return null
            }
        } catch (e: Exception) {
            return null
        }

        val decStr = value.toString()
        val hexStr = java.lang.Long.toHexString(value).uppercase()
        val binStr = java.lang.Long.toBinaryString(value)
        val octStr = java.lang.Long.toOctalString(value)

        // Format binary with spaces every 4 bits
        val paddedBin = binStr.padStart(((binStr.length + 3) / 4) * 4, '0')
        val formattedBin = paddedBin.chunked(4).joinToString(" ")

        return NumeralRepresentation(
            decimal = decStr,
            hex = "0x$hexStr",
            binary = binStr,
            octal = "0o$octStr",
            formattedBinary = formattedBin
        )
    }

    fun bitwiseOp(a: Long, b: Long, op: String): Long {
        return when (op) {
            "AND" -> a and b
            "OR" -> a or b
            "XOR" -> a xor b
            "NOT" -> a.inv()
            "SHL" -> a shl (b.toInt() and 0x3F)
            "SHR" -> a ushr (b.toInt() and 0x3F)
            else -> a
        }
    }

    private fun formatMoney(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        return DecimalFormat("#,##0", symbols).format(amount)
    }
}
