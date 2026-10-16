package com.example.ui.converters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.converters.FinancialAndScienceConverters
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// ==========================================
// 1. CURRENCY CONVERTER SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencies = FinancialAndScienceConverters.defaultCurrencies

    var amountStr by remember { mutableStateOf("100") }
    var fromCurrency by remember { mutableStateOf(currencies[0]) } // USD
    var toCurrency by remember { mutableStateOf(currencies[1]) } // EUR

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val amount = amountStr.toDoubleOrNull() ?: 0.0
    val converted = remember(amount, fromCurrency, toCurrency) {
        FinancialAndScienceConverters.convertCurrency(
            amount,
            fromCurrency.defaultRateToUsd,
            toCurrency.defaultRateToUsd
        )
    }

    val symbols = DecimalFormatSymbols(Locale.US)
    val moneyFormat = remember { DecimalFormat("#,##0.00", symbols) }
    val formattedResult = remember(converted) { moneyFormat.format(converted) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("From", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = fromExpanded,
                    onExpandedChange = { fromExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${fromCurrency.code} - ${fromCurrency.name} (${fromCurrency.symbol})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = fromExpanded,
                        onDismissRequest = { fromExpanded = false }
                    ) {
                        currencies.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.code} - ${c.name} (${c.symbol})") },
                                onClick = {
                                    fromCurrency = c
                                    fromExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalIconButton(
            onClick = {
                val temp = fromCurrency
                fromCurrency = toCurrency
                toCurrency = temp
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.SwapVert, contentDescription = "Swap Currencies")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("To", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${toCurrency.symbol} $formattedResult ${toCurrency.code}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = toExpanded,
                    onExpandedChange = { toExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${toCurrency.code} - ${toCurrency.name} (${toCurrency.symbol})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = toExpanded,
                        onDismissRequest = { toExpanded = false }
                    ) {
                        currencies.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.code} - ${c.name} (${c.symbol})") },
                                onClick = {
                                    toCurrency = c
                                    toExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exchange rate info card
        val oneFromInTo = FinancialAndScienceConverters.convertCurrency(1.0, fromCurrency.defaultRateToUsd, toCurrency.defaultRateToUsd)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "1 ${fromCurrency.code} = ${moneyFormat.format(oneFromInTo)} ${toCurrency.code}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Standard benchmark rates • Offline ready",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Currency", "$formattedResult ${toCurrency.code}"))
                    Toast.makeText(context, "Copied $formattedResult ${toCurrency.code}", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy")
            }

            Button(
                onClick = {
                    val expr = "$amountStr ${fromCurrency.code} to ${toCurrency.code}"
                    val res = "$formattedResult ${toCurrency.code}"
                    onSaveToHistory(expr, res, "currency")
                    Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save")
            }
        }
    }
}

// ==========================================
// 2. MOLARITY & CHEMISTRY SCREEN
// ==========================================
@Composable
fun MolarityConverterScreen(
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mass -> Molarity, 1: Desired Molarity -> Mass, 2: Dilution C1V1=C2V2

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Mode Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("Find Molarity") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("Find Mass") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("Dilution C₁V₁") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0 || selectedTab == 1) {
            var massStr by remember { mutableStateOf("5.84") }
            var molarityStr by remember { mutableStateOf("0.1") }
            var molarMassStr by remember { mutableStateOf("58.44") }
            var volumeStr by remember { mutableStateOf("1000") } // mL
            var isVolumeInMl by remember { mutableStateOf(true) }

            // Preset Compound Selector
            Text("Quick Presets (Compound):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FinancialAndScienceConverters.commonCompounds) { compound ->
                    FilterChip(
                        selected = molarMassStr == compound.molarMass.toString(),
                        onClick = { molarMassStr = compound.molarMass.toString() },
                        label = { Text("${compound.formula} (${compound.molarMass} g/mol)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = molarMassStr,
                onValueChange = { molarMassStr = it },
                label = { Text("Molar Mass (g/mol)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = volumeStr,
                    onValueChange = { volumeStr = it },
                    label = { Text("Volume (${if (isVolumeInMl) "mL" else "L"})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = isVolumeInMl,
                    onClick = { isVolumeInMl = !isVolumeInMl },
                    label = { Text(if (isVolumeInMl) "mL" else "Liters") },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val vLiters = (volumeStr.toDoubleOrNull() ?: 0.0) / (if (isVolumeInMl) 1000.0 else 1.0)
            val mw = molarMassStr.toDoubleOrNull() ?: 0.0

            if (selectedTab == 0) {
                // Find Molarity
                OutlinedTextField(
                    value = massStr,
                    onValueChange = { massStr = it },
                    label = { Text("Mass of Solute (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                val mass = massStr.toDoubleOrNull() ?: 0.0
                val calculatedM = FinancialAndScienceConverters.calculateMolarity(mass, mw, vLiters)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Calculated Molarity:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "${DecimalFormat("#0.0000").format(calculatedM)} M (mol/L)",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val expr = "$mass g / ($mw g/mol × ${volumeStr}${if (isVolumeInMl) "mL" else "L"})"
                        val res = "${DecimalFormat("#0.0000").format(calculatedM)} M"
                        onSaveToHistory(expr, res, "molarity")
                        Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save to History")
                }
            } else {
                // Find Required Mass
                OutlinedTextField(
                    value = molarityStr,
                    onValueChange = { molarityStr = it },
                    label = { Text("Desired Molarity (M or mol/L)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                val targetM = molarityStr.toDoubleOrNull() ?: 0.0
                val reqMass = FinancialAndScienceConverters.calculateRequiredMass(targetM, mw, vLiters)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Required Solute Mass:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "${DecimalFormat("#0.0000").format(reqMass)} g",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val expr = "$targetM M × $mw g/mol × ${volumeStr}${if (isVolumeInMl) "mL" else "L"}"
                        val res = "${DecimalFormat("#0.0000").format(reqMass)} g"
                        onSaveToHistory(expr, res, "molarity")
                        Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save to History")
                }
            }
        } else {
            // Dilution C1 * V1 = C2 * V2
            var c1Str by remember { mutableStateOf("10.0") }
            var v1Str by remember { mutableStateOf("") }
            var c2Str by remember { mutableStateOf("1.0") }
            var v2Str by remember { mutableStateOf("500.0") }

            Text("Formula: C₁ × V₁ = C₂ × V₂", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Leave one field blank to solve for it automatically.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = c1Str,
                onValueChange = { c1Str = it },
                label = { Text("Stock Concentration (C₁)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = v1Str,
                onValueChange = { v1Str = it },
                label = { Text("Stock Volume needed (V₁) - Leave blank to solve") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = c2Str,
                onValueChange = { c2Str = it },
                label = { Text("Final Concentration (C₂)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = v2Str,
                onValueChange = { v2Str = it },
                label = { Text("Final Volume (V₂)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            val c1 = c1Str.toDoubleOrNull()
            val v1 = v1Str.toDoubleOrNull()
            val c2 = c2Str.toDoubleOrNull()
            val v2 = v2Str.toDoubleOrNull()

            val solved = FinancialAndScienceConverters.solveDilution(c1, v1, c2, v2)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dilution Result:", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "${DecimalFormat("#0.000").format(solved)} units",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. INCOME TAX SCREEN
// ==========================================
@Composable
fun IncomeTaxScreen(
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var grossStr by remember { mutableStateOf("75000") }
    var filingStatus by remember { mutableStateOf(FinancialAndScienceConverters.TaxFilingStatus.SINGLE) }
    var useCustomDeduction by remember { mutableStateOf(false) }
    var customDeductionStr by remember { mutableStateOf("14600") }

    val gross = grossStr.toDoubleOrNull() ?: 0.0
    val customDed = if (useCustomDeduction) customDeductionStr.toDoubleOrNull() else null

    val taxResult = remember(gross, filingStatus, customDed) {
        FinancialAndScienceConverters.calculateIncomeTax(gross, filingStatus, customDed)
    }

    val fmt = remember { DecimalFormat("#,##0") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Annual Gross Income ($)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = grossStr,
            onValueChange = { grossStr = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Filing Status:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FinancialAndScienceConverters.TaxFilingStatus.values().forEach { st ->
                FilterChip(
                    selected = filingStatus == st,
                    onClick = { filingStatus = st },
                    label = { Text(st.label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Summary Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Estimated Federal Tax", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "$${fmt.format(taxResult.totalTax)}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Effective Tax Rate", style = MaterialTheme.typography.labelSmall)
                        Text("${DecimalFormat("#0.1").format(taxResult.effectiveRatePercent)}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column {
                        Text("Marginal Rate", style = MaterialTheme.typography.labelSmall)
                        Text("${DecimalFormat("#0.0").format(taxResult.marginalRatePercent)}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Net Monthly Take-Home", style = MaterialTheme.typography.labelSmall)
                        Text("$${fmt.format(taxResult.netTakeHomeMonthly)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Column {
                        Text("Net Annual Take-Home", style = MaterialTheme.typography.labelSmall)
                        Text("$${fmt.format(taxResult.netTakeHomeAnnual)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Breakdown Table Card
        Text("Progressive Tax Brackets Breakdown:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                taxResult.brackets.forEach { b ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${b.ratePercent.toInt()}% on $${fmt.format(b.amountInBracket)}", style = MaterialTheme.typography.bodyMedium)
                        Text("$${fmt.format(b.taxInBracket)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val expr = "Tax on $grossStr (${filingStatus.label})"
                val res = "Tax: $${fmt.format(taxResult.totalTax)} (Net: $${fmt.format(taxResult.netTakeHomeAnnual)})"
                onSaveToHistory(expr, res, "tax")
                Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Tax Estimate to History")
        }
    }
}

// ==========================================
// 4. MORTGAGE SCREEN
// ==========================================
@Composable
fun MortgageScreen(
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var homePriceStr by remember { mutableStateOf("400000") }
    var downPaymentStr by remember { mutableStateOf("80000") }
    var interestRateStr by remember { mutableStateOf("6.5") }
    var termYears by remember { mutableIntStateOf(30) }

    val homePrice = homePriceStr.toDoubleOrNull() ?: 0.0
    val downPayment = downPaymentStr.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateStr.toDoubleOrNull() ?: 0.0

    val mortgage = remember(homePrice, downPayment, interestRate, termYears) {
        FinancialAndScienceConverters.calculateMortgage(homePrice, downPayment, interestRate, termYears)
    }

    val fmt = remember { DecimalFormat("#,##0") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = homePriceStr,
            onValueChange = { homePriceStr = it },
            label = { Text("Home Price ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = downPaymentStr,
                onValueChange = { downPaymentStr = it },
                label = { Text("Down Payment ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = interestRateStr,
                onValueChange = { interestRateStr = it },
                label = { Text("Interest Rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Loan Term:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(15, 20, 30).forEach { yr ->
                FilterChip(
                    selected = termYears == yr,
                    onClick = { termYears = yr },
                    label = { Text("$yr Years") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Result Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Monthly Principal & Interest", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "$${fmt.format(mortgage.monthlyPrincipalAndInterest)}/mo",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Loan Amount", style = MaterialTheme.typography.labelSmall)
                        Text("$${fmt.format(mortgage.loanAmount)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column {
                        Text("Total Interest Paid", style = MaterialTheme.typography.labelSmall)
                        Text("$${fmt.format(mortgage.totalInterest)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("Total Loan Cost", style = MaterialTheme.typography.labelSmall)
                    Text("$${fmt.format(mortgage.totalCost)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val expr = "$$homePriceStr home, $$downPaymentStr down at $interestRateStr% for $termYears yrs"
                val res = "$${fmt.format(mortgage.monthlyPrincipalAndInterest)}/mo (Total Int: $${fmt.format(mortgage.totalInterest)})"
                onSaveToHistory(expr, res, "mortgage")
                Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Mortgage Estimate")
        }
    }
}

// ==========================================
// 5. NUMERAL SYSTEM CONVERTER SCREEN
// ==========================================
@Composable
fun NumeralSystemScreen(
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var baseMode by remember { mutableIntStateOf(10) } // 10, 16, 2, 8
    var inputStr by remember { mutableStateOf("255") }

    val representation = remember(inputStr, baseMode) {
        FinancialAndScienceConverters.convertNumeral(inputStr, baseMode)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Input Base:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10 to "DEC (10)", 16 to "HEX (16)", 2 to "BIN (2)", 8 to "OCT (8)").forEach { (base, label) ->
                FilterChip(
                    selected = baseMode == base,
                    onClick = { baseMode = base },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = inputStr,
            onValueChange = { inputStr = it },
            label = { Text("Enter Number in Base $baseMode") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (representation != null) {
            // Live Representations Cards
            listOf(
                "Decimal (Base 10)" to representation.decimal,
                "Hexadecimal (Base 16)" to representation.hex,
                "Binary (Base 2)" to representation.formattedBinary,
                "Octal (Base 8)" to representation.octal
            ).forEach { (label, value) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
                            Toast.makeText(context, "Copied $value", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                value,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val expr = "$inputStr (Base $baseMode)"
                    val res = "HEX: ${representation.hex}, BIN: ${representation.formattedBinary}"
                    onSaveToHistory(expr, res, "numeral")
                    Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Conversion to History")
            }
        } else {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Invalid number for Base $baseMode",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// ==========================================
// 6. BMI CONVERTER SCREEN
// ==========================================
@Composable
fun BmiScreen(
    onSaveToHistory: (expression: String, result: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isImperial by remember { mutableStateOf(false) }

    // Metric states
    var weightKgStr by remember { mutableStateOf("70") }
    var heightCmStr by remember { mutableStateOf("175") }

    // Imperial states
    var weightLbStr by remember { mutableStateOf("154") }
    var heightFtStr by remember { mutableStateOf("5") }
    var heightInStr by remember { mutableStateOf("9") }

    val calculatedWeightKg = if (isImperial) {
        (weightLbStr.toDoubleOrNull() ?: 0.0) * 0.45359237
    } else {
        weightKgStr.toDoubleOrNull() ?: 0.0
    }

    val calculatedHeightCm = if (isImperial) {
        val ft = heightFtStr.toDoubleOrNull() ?: 0.0
        val inches = heightInStr.toDoubleOrNull() ?: 0.0
        (ft * 12.0 + inches) * 2.54
    } else {
        heightCmStr.toDoubleOrNull() ?: 0.0
    }

    val bmiResult = remember(calculatedWeightKg, calculatedHeightCm) {
        FinancialAndScienceConverters.calculateBmiMetric(calculatedWeightKg, calculatedHeightCm)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Unit System", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isImperial) "Imperial (lb, ft)" else "Metric (kg, cm)")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = isImperial, onCheckedChange = { isImperial = it })
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!isImperial) {
            OutlinedTextField(
                value = weightKgStr,
                onValueChange = { weightKgStr = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = heightCmStr,
                onValueChange = { heightCmStr = it },
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = weightLbStr,
                onValueChange = { weightLbStr = it },
                label = { Text("Weight (lbs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heightFtStr,
                    onValueChange = { heightFtStr = it },
                    label = { Text("Feet (ft)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = heightInStr,
                    onValueChange = { heightInStr = it },
                    label = { Text("Inches (in)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (bmiResult != null) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your Body Mass Index", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DecimalFormat("#0.1").format(bmiResult.bmi),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(bmiResult.category.colorHex))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = bmiResult.category.label,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Healthy weight for your height: ${DecimalFormat("#0.0").format(bmiResult.healthyWeightMinKg)} - ${DecimalFormat("#0.0").format(bmiResult.healthyWeightMaxKg)} kg (${DecimalFormat("#0.0").format(bmiResult.healthyWeightMinLb)} - ${DecimalFormat("#0.0").format(bmiResult.healthyWeightMaxLb)} lbs)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val expr = "${if (isImperial) "$weightLbStr lbs, $heightFtStr'$heightInStr\"" else "$weightKgStr kg, $heightCmStr cm"}"
                    val res = "BMI ${DecimalFormat("#0.1").format(bmiResult.bmi)} (${bmiResult.category.label})"
                    onSaveToHistory(expr, res, "bmi")
                    Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save BMI to History")
            }
        }
    }
}
