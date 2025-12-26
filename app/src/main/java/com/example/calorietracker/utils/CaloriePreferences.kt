package com.example.calorietracker.utils

import android.content.Context
import android.content.SharedPreferences

class CaloriePreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("nutra_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_CALORIE_GOAL = "calorie_goal"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }
    
    fun setCalorieGoal(goal: Int) {
        prefs.edit().putInt(KEY_CALORIE_GOAL, goal).apply()
    }
    
    fun getCalorieGoal(): Int {
        return prefs.getInt(KEY_CALORIE_GOAL, 0)
    }
    
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }
    
    fun setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }
}
