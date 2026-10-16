package com.example.currency

import android.content.Context
import com.example.converters.FinancialAndScienceConverters
import com.example.data.CurrencyRateDao
import com.example.data.CurrencyRateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String
)

class CurrencyRepository(
    private val currencyRateDao: CurrencyRateDao,
    private val context: Context
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    // 24 hours in milliseconds for daily updates
    private val dailyUpdateInterval = 24 * 60 * 60 * 1000L

    val allRatesFlow: Flow<Map<String, Double>> = currencyRateDao.getAllRatesFlow().map { list ->
        if (list.isEmpty()) {
            FinancialAndScienceConverters.defaultCurrencies.associate { it.code to it.defaultRateToUsd }
        } else {
            val map = list.associate { it.code to it.rateToUsd }.toMutableMap()
            map["USD"] = 1.0
            map
        }
    }

    suspend fun getLatestUpdateTime(): Long {
        return currencyRateDao.getLatestUpdateTime() ?: 0L
    }

    suspend fun shouldUpdateDaily(): Boolean {
        val lastUpdate = getLatestUpdateTime()
        val now = System.currentTimeMillis()
        return (now - lastUpdate) > dailyUpdateInterval || lastUpdate == 0L
    }

    suspend fun updateDailyRates(force: Boolean = false): Result<Int> = withContext(Dispatchers.IO) {
        if (!force && !shouldUpdateDaily()) {
            // Already updated within the last 24 hours
            val existing = currencyRateDao.getAllRates()
            return@withContext Result.success(existing.size)
        }

        try {
            // Try Primary API: open.er-api.com
            val primaryUrl = "https://open.er-api.com/v6/latest/USD"
            val request = Request.Builder().url(primaryUrl).build()
            val response = client.newCall(request).execute()

            val ratesMap = mutableMapOf<String, Double>()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                if (json.has("rates")) {
                    val ratesObj = json.getJSONObject("rates")
                    val keys = ratesObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        ratesMap[key] = ratesObj.getDouble(key)
                    }
                }
            }

            // Fallback to Frankfurter API if primary is empty or failed
            if (ratesMap.isEmpty()) {
                val backupUrl = "https://api.frankfurter.dev/v1/latest?base=USD"
                val backupReq = Request.Builder().url(backupUrl).build()
                val backupResp = client.newCall(backupReq).execute()
                if (backupResp.isSuccessful) {
                    val body = backupResp.body?.string() ?: ""
                    val json = JSONObject(body)
                    if (json.has("rates")) {
                        val ratesObj = json.getJSONObject("rates")
                        val keys = ratesObj.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            ratesMap[key] = ratesObj.getDouble(key)
                        }
                    }
                }
            }

            if (ratesMap.isNotEmpty()) {
                ratesMap["USD"] = 1.0
                val now = System.currentTimeMillis()
                val entities = ratesMap.map { (code, rate) ->
                    CurrencyRateEntity(
                        code = code,
                        rateToUsd = rate,
                        lastUpdatedMillis = now
                    )
                }
                currencyRateDao.insertRates(entities)
                Result.success(entities.size)
            } else {
                // Ensure default values exist in DB if totally empty
                seedDefaultsIfEmpty()
                Result.failure(Exception("Could not retrieve latest rates from network"))
            }
        } catch (e: Exception) {
            // Offline or network error: seed default if DB empty
            seedDefaultsIfEmpty()
            Result.failure(e)
        }
    }

    private suspend fun seedDefaultsIfEmpty() {
        val current = currencyRateDao.getAllRates()
        if (current.isEmpty()) {
            val now = System.currentTimeMillis()
            val initial = FinancialAndScienceConverters.defaultCurrencies.map {
                CurrencyRateEntity(
                    code = it.code,
                    rateToUsd = it.defaultRateToUsd,
                    lastUpdatedMillis = now
                )
            }
            currencyRateDao.insertRates(initial)
        }
    }

    companion object {
        val currencyMetadata = mapOf(
            "USD" to CurrencyInfo("USD", "US Dollar", "$"),
            "EUR" to CurrencyInfo("EUR", "Euro", "€"),
            "GBP" to CurrencyInfo("GBP", "British Pound", "£"),
            "JPY" to CurrencyInfo("JPY", "Japanese Yen", "¥"),
            "CAD" to CurrencyInfo("CAD", "Canadian Dollar", "CA$"),
            "AUD" to CurrencyInfo("AUD", "Australian Dollar", "A$"),
            "CHF" to CurrencyInfo("CHF", "Swiss Franc", "CHF"),
            "CNY" to CurrencyInfo("CNY", "Chinese Yuan", "¥"),
            "INR" to CurrencyInfo("INR", "Indian Rupee", "₹"),
            "BRL" to CurrencyInfo("BRL", "Brazilian Real", "R$"),
            "MXN" to CurrencyInfo("MXN", "Mexican Peso", "Mex$"),
            "KRW" to CurrencyInfo("KRW", "South Korean Won", "₩"),
            "SGD" to CurrencyInfo("SGD", "Singapore Dollar", "S$"),
            "NZD" to CurrencyInfo("NZD", "New Zealand Dollar", "NZ$"),
            "HKD" to CurrencyInfo("HKD", "Hong Kong Dollar", "HK$"),
            "SEK" to CurrencyInfo("SEK", "Swedish Krona", "kr"),
            "NOK" to CurrencyInfo("NOK", "Norwegian Krone", "kr"),
            "TRY" to CurrencyInfo("TRY", "Turkish Lira", "₺"),
            "ZAR" to CurrencyInfo("ZAR", "South African Rand", "R"),
            "AED" to CurrencyInfo("AED", "UAE Dirham", "AED"),
            "SAR" to CurrencyInfo("SAR", "Saudi Riyal", "SAR"),
            "THB" to CurrencyInfo("THB", "Thai Baht", "฿"),
            "IDR" to CurrencyInfo("IDR", "Indonesian Rupiah", "Rp"),
            "MYR" to CurrencyInfo("MYR", "Malaysian Ringgit", "RM"),
            "PHP" to CurrencyInfo("PHP", "Philippine Peso", "₱"),
            "PLN" to CurrencyInfo("PLN", "Polish Zloty", "zł"),
            "DKK" to CurrencyInfo("DKK", "Danish Krone", "kr"),
            "CZK" to CurrencyInfo("CZK", "Czech Koruna", "Kč"),
            "HUF" to CurrencyInfo("HUF", "Hungarian Forint", "Ft"),
            "ILS" to CurrencyInfo("ILS", "Israeli Shekel", "₪"),
            "CLP" to CurrencyInfo("CLP", "Chilean Peso", "CLP$"),
            "TWD" to CurrencyInfo("TWD", "New Taiwan Dollar", "NT$")
        )
    }
}
