package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE category = :category ORDER BY timestamp DESC")
    fun getHistoryByCategory(category: String): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CalculationEntity): Long

    @Delete
    suspend fun delete(entity: CalculationEntity)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()
}
