package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val category: String, // "basic", "scientific", "currency", "length", "bmi", etc.
    val timestamp: Long = System.currentTimeMillis()
)
