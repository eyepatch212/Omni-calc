package com.example.currency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CurrencyUiState(
    val amountString: String = "100",
    val fromCurrency: CurrencyInfo = CurrencyRepository.currencyMetadata["USD"] ?: CurrencyInfo("USD", "US Dollar", "$"),
    val toCurrency: CurrencyInfo = CurrencyRepository.currencyMetadata["EUR"] ?: CurrencyInfo("EUR", "Euro", "€"),
    val rates: Map<String, Double> = emptyMap(),
    val availableCurrencies: List<CurrencyInfo> = emptyList(),
    val isUpdating: Boolean = false,
    val lastUpdatedText: String = "Checking daily rates...",
    val lastUpdatedMillis: Long = 0L,
    val statusMessage: String? = null
) {
    val fromRate: Double get() = rates[fromCurrency.code] ?: 1.0
    val toRate: Double get() = rates[toCurrency.code] ?: 1.0

    val numericAmount: Double get() = amountString.toDoubleOrNull() ?: 0.0

    val convertedAmount: Double
        get() {
            if (fromRate <= 0.0) return 0.0
            val amountInUsd = numericAmount / fromRate
            return amountInUsd * toRate
        }

    val formattedResult: String
        get() {
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("#,##0.00", symbols)
            return df.format(convertedAmount)
        }

    val unitExchangeRate: Double
        get() {
            if (fromRate <= 0.0) return 0.0
            return (1.0 / fromRate) * toRate
        }
}

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CurrencyRepository
    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = CurrencyRepository(db.currencyRateDao(), application)

        // Populate available currencies from metadata
        val allCurrencies = CurrencyRepository.currencyMetadata.values.toList()
            .sortedBy { it.code }

        _uiState.update { it.copy(availableCurrencies = allCurrencies) }

        // Observe rates from Room
        viewModelScope.launch {
            repository.allRatesFlow.collect { ratesMap ->
                val lastUpdate = repository.getLatestUpdateTime()
                val dateText = formatLastUpdated(lastUpdate)
                _uiState.update {
                    it.copy(
                        rates = ratesMap,
                        lastUpdatedMillis = lastUpdate,
                        lastUpdatedText = dateText
                    )
                }
            }
        }

        // Automatic daily update check on initialization
        checkAndTriggerDailyUpdate()
    }

    fun checkAndTriggerDailyUpdate() {
        viewModelScope.launch {
            if (repository.shouldUpdateDaily()) {
                refreshRates(force = false)
            }
        }
    }

    fun refreshRates(force: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, statusMessage = "Updating exchange rates...") }
            val result = repository.updateDailyRates(force = force)
            if (result.isSuccess) {
                val count = result.getOrNull() ?: 0
                val lastUpdate = repository.getLatestUpdateTime()
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        lastUpdatedMillis = lastUpdate,
                        lastUpdatedText = formatLastUpdated(lastUpdate),
                        statusMessage = "Updated daily rates ($count currencies)"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        statusMessage = "Using latest cached rates (Offline)"
                    )
                }
            }
        }
    }

    fun setAmount(newAmount: String) {
        _uiState.update { it.copy(amountString = newAmount) }
    }

    fun setFromCurrency(currency: CurrencyInfo) {
        _uiState.update { it.copy(fromCurrency = currency) }
    }

    fun setToCurrency(currency: CurrencyInfo) {
        _uiState.update { it.copy(toCurrency = currency) }
    }

    fun swapCurrencies() {
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency
            )
        }
    }

    private fun formatLastUpdated(millis: Long): String {
        if (millis == 0L) return "Rates not updated yet"
        val now = System.currentTimeMillis()
        val diff = now - millis
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        val formattedDate = sdf.format(Date(millis))

        return if (diff < 60 * 60 * 1000L) {
            "Updated recently (Today)"
        } else if (diff < 24 * 60 * 60 * 1000L) {
            "Updated today at ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))}"
        } else {
            "Updated $formattedDate"
        }
    }
}
