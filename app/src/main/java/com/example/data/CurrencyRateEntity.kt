package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val code: String,
    val rateToUsd: Double,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
