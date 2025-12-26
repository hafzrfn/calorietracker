package com.example.calorietracker.database.dao

import androidx.room.*
import com.example.calorietracker.database.entities.FoodEntry

@Dao
interface FoodEntryDao {
    @Insert
    suspend fun insertFoodEntry(foodEntry: FoodEntry): Long
    
    @Query("SELECT * FROM food_entries WHERE userId = :userId ORDER BY entryDate DESC")
    suspend fun getAllEntriesForUser(userId: Int): List<FoodEntry>
    
    @Query("""
        SELECT * FROM food_entries 
        WHERE userId = :userId 
        AND date(entryDate / 1000, 'unixepoch', 'localtime') = date(:date / 1000, 'unixepoch', 'localtime')
        ORDER BY entryDate DESC
    """)
    suspend fun getEntriesForUserByDate(userId: Int, date: Long): List<FoodEntry>
    
    @Delete
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)
    
    @Query("DELETE FROM food_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Int)
    
    @Query("""
        SELECT SUM(calories) FROM food_entries 
        WHERE userId = :userId 
        AND date(entryDate / 1000, 'unixepoch', 'localtime') = date(:date / 1000, 'unixepoch', 'localtime')
    """)
    suspend fun getTotalCaloriesForDate(userId: Int, date: Long): Int?
    
    @Query("""
        SELECT SUM(carbs) FROM food_entries 
        WHERE userId = :userId 
        AND date(entryDate / 1000, 'unixepoch', 'localtime') = date(:date / 1000, 'unixepoch', 'localtime')
    """)
    suspend fun getTotalCarbsForDate(userId: Int, date: Long): Int?
    
    @Query("""
        SELECT SUM(protein) FROM food_entries 
        WHERE userId = :userId 
        AND date(entryDate / 1000, 'unixepoch', 'localtime') = date(:date / 1000, 'unixepoch', 'localtime')
    """)
    suspend fun getTotalProteinForDate(userId: Int, date: Long): Int?
    
    @Query("""
        SELECT SUM(fats) FROM food_entries 
        WHERE userId = :userId 
        AND date(entryDate / 1000, 'unixepoch', 'localtime') = date(:date / 1000, 'unixepoch', 'localtime')
    """)
    suspend fun getTotalFatsForDate(userId: Int, date: Long): Int?
}
