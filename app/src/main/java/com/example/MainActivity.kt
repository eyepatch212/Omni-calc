package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.CalculatorViewModel
import com.example.converters.ConverterCategory
import com.example.data.AppDatabase
import com.example.data.HistoryRepository
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.converters.ConverterDetailHost
import com.example.ui.converters.ConvertersHubScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class MainNavTab {
    CALCULATOR,
    CONVERTERS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                OmniCalcApp()
            }
        }
    }
}

@Composable
fun OmniCalcApp() {
    val calculatorViewModel: CalculatorViewModel = viewModel()
    var currentTab by remember { mutableStateOf(MainNavTab.CALCULATOR) }
    var selectedConverter by remember { mutableStateOf<ConverterCategory?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val historyRepo = remember {
        val db = AppDatabase.getInstance(context)
        HistoryRepository(db.calculationDao())
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar when on main tabs or not inside a sub-converter
            if (selectedConverter == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainNavTab.CALCULATOR,
                        onClick = { currentTab = MainNavTab.CALCULATOR },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainNavTab.CALCULATOR) Icons.Filled.Calculate else Icons.Outlined.Calculate,
                                contentDescription = "Calculator"
                            )
                        },
                        label = { Text("Calculator") },
                        modifier = Modifier.testTag("nav_tab_calculator")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainNavTab.CONVERTERS,
                        onClick = { currentTab = MainNavTab.CONVERTERS },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainNavTab.CONVERTERS) Icons.Filled.SwapHoriz else Icons.Outlined.SwapHoriz,
                                contentDescription = "Converters"
                            )
                        },
                        label = { Text("Converters") },
                        modifier = Modifier.testTag("nav_tab_converters")
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.surface
        ) {
            when (currentTab) {
                MainNavTab.CALCULATOR -> {
                    CalculatorScreen(viewModel = calculatorViewModel)
                }
                MainNavTab.CONVERTERS -> {
                    if (selectedConverter == null) {
                        ConvertersHubScreen(
                            onSelectConverter = { category ->
                                selectedConverter = category
                            }
                        )
                    } else {
                        ConverterDetailHost(
                            category = selectedConverter!!,
                            onBack = { selectedConverter = null },
                            onSaveToHistory = { expr, res, cat ->
                                coroutineScope.launch {
                                    historyRepo.addCalculation(expr, res, cat)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
