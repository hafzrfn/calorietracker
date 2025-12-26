package com.example.calorietracker.repository

import com.example.calorietracker.database.dao.FoodEntryDao
import com.example.calorietracker.database.entities.FoodEntry

class FoodRepository(private val foodEntryDao: FoodEntryDao) {
    
    suspend fun addFoodEntry(
        userId: Int,
        foodName: String,
        calories: Int,
        carbs: Int,
        protein: Int,
        fats: Int,
        date: Long = System.currentTimeMillis()
    ): Long {
        val entry = FoodEntry(
            userId = userId,
            foodName = foodName,
            calories = calories,
            carbs = carbs,
            protein = protein,
            fats = fats,
            entryDate = date
        )
        return foodEntryDao.insertFoodEntry(entry)
    }
    
    suspend fun getFoodEntriesForDate(userId: Int, date: Long): List<FoodEntry> {
        return foodEntryDao.getEntriesForUserByDate(userId, date)
    }
    
    suspend fun getAllFoodEntries(userId: Int): List<FoodEntry> {
        return foodEntryDao.getAllEntriesForUser(userId)
    }
    
    suspend fun deleteFoodEntry(entryId: Int) {
        foodEntryDao.deleteEntryById(entryId)
    }
    
    suspend fun getDailyTotals(userId: Int, date: Long): DailyTotals {
        return DailyTotals(
            calories = foodEntryDao.getTotalCaloriesForDate(userId, date) ?: 0,
            carbs = foodEntryDao.getTotalCarbsForDate(userId, date) ?: 0,
            protein = foodEntryDao.getTotalProteinForDate(userId, date) ?: 0,
            fats = foodEntryDao.getTotalFatsForDate(userId, date) ?: 0
        )
    }
}

data class DailyTotals(
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fats: Int
)
