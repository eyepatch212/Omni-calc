package com.example.converters

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object UnitConverters {

    // LENGTH (Base unit: Meter)
    val lengthUnits = listOf(
        UnitDefinition("m", "Meter", "m", 1.0),
        UnitDefinition("km", "Kilometer", "km", 1000.0),
        UnitDefinition("cm", "Centimeter", "cm", 0.01),
        UnitDefinition("mm", "Millimeter", "mm", 0.001),
        UnitDefinition("um", "Micrometer", "µm", 1e-6),
        UnitDefinition("nm", "Nanometer", "nm", 1e-9),
        UnitDefinition("in", "Inch", "in", 0.0254),
        UnitDefinition("ft", "Foot", "ft", 0.3048),
        UnitDefinition("yd", "Yard", "yd", 0.9144),
        UnitDefinition("mi", "Mile", "mi", 1609.344),
        UnitDefinition("nmi", "Nautical Mile", "NM", 1852.0)
    )

    // MASS (Base unit: Kilogram)
    val massUnits = listOf(
        UnitDefinition("kg", "Kilogram", "kg", 1.0),
        UnitDefinition("g", "Gram", "g", 0.001),
        UnitDefinition("mg", "Milligram", "mg", 1e-6),
        UnitDefinition("t", "Metric Ton", "t", 1000.0),
        UnitDefinition("lb", "Pound", "lb", 0.45359237),
        UnitDefinition("oz", "Ounce", "oz", 0.028349523125),
        UnitDefinition("st", "Stone", "st", 6.35029318),
        UnitDefinition("ct", "Carat", "ct", 0.0002)
    )

    // AREA (Base unit: Square Meter)
    val areaUnits = listOf(
        UnitDefinition("m2", "Square Meter", "m²", 1.0),
        UnitDefinition("km2", "Square Kilometer", "km²", 1e6),
        UnitDefinition("cm2", "Square Centimeter", "cm²", 0.0001),
        UnitDefinition("mm2", "Square Millimeter", "mm²", 1e-6),
        UnitDefinition("ha", "Hectare", "ha", 10000.0),
        UnitDefinition("ac", "Acre", "ac", 4046.8564224),
        UnitDefinition("ft2", "Square Foot", "ft²", 0.09290304),
        UnitDefinition("in2", "Square Inch", "in²", 0.00064516),
        UnitDefinition("yd2", "Square Yard", "yd²", 0.83612736),
        UnitDefinition("mi2", "Square Mile", "mi²", 2589988.110336)
    )

    // VOLUME (Base unit: Liter)
    val volumeUnits = listOf(
        UnitDefinition("l", "Liter", "L", 1.0),
        UnitDefinition("ml", "Milliliter", "mL", 0.001),
        UnitDefinition("m3", "Cubic Meter", "m³", 1000.0),
        UnitDefinition("cm3", "Cubic Centimeter", "cm³", 0.001),
        UnitDefinition("gal_us", "Gallon (US)", "gal", 3.785411784),
        UnitDefinition("qt_us", "Quart (US)", "qt", 0.946352946),
        UnitDefinition("pt_us", "Pint (US)", "pt", 0.473176473),
        UnitDefinition("cup_us", "Cup (US)", "cup", 0.2365882365),
        UnitDefinition("floz_us", "Fluid Ounce (US)", "fl oz", 0.0295735295625),
        UnitDefinition("tbsp_us", "Tablespoon (US)", "tbsp", 0.01478676478125),
        UnitDefinition("tsp_us", "Teaspoon (US)", "tsp", 0.00492892159375),
        UnitDefinition("ft3", "Cubic Foot", "ft³", 28.316846592),
        UnitDefinition("in3", "Cubic Inch", "in³", 0.016387064),
        UnitDefinition("gal_uk", "Gallon (UK)", "gal (UK)", 4.54609)
    )

    // TIME (Base unit: Second)
    val timeUnits = listOf(
        UnitDefinition("s", "Second", "s", 1.0),
        UnitDefinition("ms", "Millisecond", "ms", 0.001),
        UnitDefinition("min", "Minute", "min", 60.0),
        UnitDefinition("h", "Hour", "h", 3600.0),
        UnitDefinition("d", "Day", "d", 86400.0),
        UnitDefinition("wk", "Week", "wk", 604800.0),
        UnitDefinition("mo", "Month (30.44 d)", "mo", 2629746.0),
        UnitDefinition("yr", "Year (365.25 d)", "yr", 31557600.0),
        UnitDefinition("dec", "Decade", "dec", 315576000.0),
        UnitDefinition("cent", "Century", "cent", 3155760000.0)
    )

    // SPEED (Base unit: Meter per second)
    val speedUnits = listOf(
        UnitDefinition("mps", "Meter per second", "m/s", 1.0),
        UnitDefinition("kph", "Kilometer per hour", "km/h", 0.2777777778),
        UnitDefinition("mph", "Mile per hour", "mph", 0.44704),
        UnitDefinition("knot", "Knot", "kn", 0.5144444444),
        UnitDefinition("fps", "Foot per second", "ft/s", 0.3048),
        UnitDefinition("mach", "Mach (at sea level)", "Mach", 340.29),
        UnitDefinition("c", "Speed of Light", "c", 299792458.0)
    )

    fun convertLinear(value: Double, from: UnitDefinition, to: UnitDefinition): Double {
        if (from.id == to.id) return value
        val baseValue = value * from.factorToBase
        return baseValue / to.factorToBase
    }

    // TEMPERATURE
    enum class TempUnit(val title: String, val symbol: String) {
        CELSIUS("Celsius", "°C"),
        FAHRENHEIT("Fahrenheit", "°F"),
        KELVIN("Kelvin", "K"),
        RANKINE("Rankine", "°R")
    }

    fun convertTemperature(value: Double, from: TempUnit, to: TempUnit): Double {
        if (from == to) return value
        // Convert 'from' to Kelvin first
        val kelvin = when (from) {
            TempUnit.CELSIUS -> value + 273.15
            TempUnit.FAHRENHEIT -> (value + 459.67) * 5.0 / 9.0
            TempUnit.KELVIN -> value
            TempUnit.RANKINE -> value * 5.0 / 9.0
        }
        // Convert Kelvin to 'to'
        return when (to) {
            TempUnit.CELSIUS -> kelvin - 273.15
            TempUnit.FAHRENHEIT -> (kelvin * 9.0 / 5.0) - 459.67
            TempUnit.KELVIN -> kelvin
            TempUnit.RANKINE -> kelvin * 9.0 / 5.0
        }
    }

    fun formatNumber(value: Double, decimals: Int = 6): String {
        if (value.isNaN() || value.isInfinite()) return value.toString()
        val absVal = kotlin.math.abs(value)
        if (absVal != 0.0 && (absVal >= 1e9 || absVal < 1e-5)) {
            val symbols = DecimalFormatSymbols(Locale.US)
            return DecimalFormat("0.######E0", symbols).format(value)
        }
        val bd = BigDecimal(value.toString())
            .setScale(decimals, RoundingMode.HALF_UP)
            .stripTrailingZeros()
        return bd.toPlainString()
    }
}
