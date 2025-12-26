package com.example.calorietracker.models

data class FoodItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fats: Int
)
