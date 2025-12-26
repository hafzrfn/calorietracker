package com.example.calorietracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_entries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val foodName: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fats: Int,
    val entryDate: Long = System.currentTimeMillis()
)
