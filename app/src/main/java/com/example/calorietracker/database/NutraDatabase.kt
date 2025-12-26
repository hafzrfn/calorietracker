package com.example.calorietracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calorietracker.database.dao.FoodEntryDao
import com.example.calorietracker.database.dao.UserDao
import com.example.calorietracker.database.entities.FoodEntry
import com.example.calorietracker.database.entities.User

@Database(
    entities = [User::class, FoodEntry::class],
    version = 1,
    exportSchema = false
)
abstract class NutraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodEntryDao(): FoodEntryDao
    
    companion object {
        @Volatile
        private var INSTANCE: NutraDatabase? = null
        
        fun getDatabase(context: Context): NutraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutraDatabase::class.java,
                    "nutra_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
