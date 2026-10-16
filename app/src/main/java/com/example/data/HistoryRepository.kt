package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val calculationDao: CalculationDao) {
    val allHistory: Flow<List<CalculationEntity>> = calculationDao.getAllHistory()

    fun getHistoryByCategory(category: String): Flow<List<CalculationEntity>> {
        return calculationDao.getHistoryByCategory(category)
    }

    suspend fun addCalculation(expression: String, result: String, category: String = "basic") {
        if (expression.isBlank() || result.isBlank()) return
        calculationDao.insert(
            CalculationEntity(
                expression = expression,
                result = result,
                category = category
            )
        )
    }

    suspend fun deleteCalculation(entity: CalculationEntity) {
        calculationDao.delete(entity)
    }

    suspend fun clearAllHistory() {
        calculationDao.clearAll()
    }
}
