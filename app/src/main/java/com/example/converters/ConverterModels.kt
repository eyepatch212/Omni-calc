package com.example.converters

import androidx.compose.ui.graphics.vector.ImageVector

enum class ConverterCategory(
    val id: String,
    val title: String,
    val description: String
) {
    CURRENCY("currency", "Currency", "Exchange rates for global currencies"),
    LENGTH("length", "Length", "Meters, feet, miles, inches, km, etc."),
    MASS("mass", "Mass & Weight", "Kilograms, pounds, grams, ounces, stones"),
    MOLARITY("molarity", "Molarity & Chemistry", "Solution prep, dilution C1V1=C2V2, molar mass"),
    AREA("area", "Area", "Square meters, acres, hectares, sq ft"),
    INCOME_TAX("income_tax", "Income Tax", "Progressive tax brackets, deductions & net pay"),
    MORTGAGE("mortgage", "Mortgage & Loan", "Monthly payments, interest, amortization"),
    TIME("time", "Time", "Seconds, minutes, hours, days, years"),
    VOLUME("volume", "Volume", "Liters, gallons, milliliters, cups, fl oz"),
    NUMERAL_SYSTEM("numeral_system", "Numeral Systems", "Binary, decimal, hexadecimal, octal"),
    TEMPERATURE("temperature", "Temperature", "Celsius, Fahrenheit, Kelvin, Rankine"),
    SPEED("speed", "Speed", "km/h, mph, m/s, knots, Mach"),
    BMI("bmi", "BMI (Body Mass Index)", "Weight status, ideal weight range")
}

data class UnitDefinition(
    val id: String,
    val name: String,
    val symbol: String,
    val factorToBase: Double // Factor to convert this unit into the standard base unit
)
