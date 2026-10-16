package com.example.ui.converters

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.converters.ConverterCategory
import com.example.converters.UnitConverters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterDetailHost(
    category: ConverterCategory,
    onBack: () -> Unit,
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (category) {
                ConverterCategory.LENGTH -> StandardUnitConverter(
                    title = "Length",
                    units = UnitConverters.lengthUnits,
                    initialFromUnitIndex = 0, // Meter
                    initialToUnitIndex = 7, // Foot
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.MASS -> StandardUnitConverter(
                    title = "Mass",
                    units = UnitConverters.massUnits,
                    initialFromUnitIndex = 0, // Kilogram
                    initialToUnitIndex = 4, // Pound
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.AREA -> StandardUnitConverter(
                    title = "Area",
                    units = UnitConverters.areaUnits,
                    initialFromUnitIndex = 0, // Square meter
                    initialToUnitIndex = 6, // Square foot
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.VOLUME -> StandardUnitConverter(
                    title = "Volume",
                    units = UnitConverters.volumeUnits,
                    initialFromUnitIndex = 0, // Liter
                    initialToUnitIndex = 4, // Gallon
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.TIME -> StandardUnitConverter(
                    title = "Time",
                    units = UnitConverters.timeUnits,
                    initialFromUnitIndex = 2, // Minute
                    initialToUnitIndex = 3, // Hour
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.SPEED -> StandardUnitConverter(
                    title = "Speed",
                    units = UnitConverters.speedUnits,
                    initialFromUnitIndex = 1, // km/h
                    initialToUnitIndex = 2, // mph
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.TEMPERATURE -> TemperatureConverter(
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.CURRENCY -> CurrencyConverterScreen(
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.MOLARITY -> MolarityConverterScreen(
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.INCOME_TAX -> IncomeTaxScreen(
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.MORTGAGE -> MortgageScreen(
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.NUMERAL_SYSTEM -> NumeralSystemScreen(
                    onSaveToHistory = onSaveToHistory
                )
                ConverterCategory.BMI -> BmiScreen(
                    onSaveToHistory = onSaveToHistory
                )
            }
        }
    }
}
