package com.example.calorietracker.repository

import com.example.calorietracker.database.dao.UserDao
import com.example.calorietracker.database.entities.User
import com.example.calorietracker.utils.PasswordUtils

class UserRepository(private val userDao: UserDao) {
    
    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        calorieGoal: Int
    ): Result<Long> {
        return try {
            // Check if username already exists
            if (userDao.usernameExists(username)) {
                return Result.failure(Exception("Username already exists"))
            }
            
            // Check if email already exists
            if (userDao.emailExists(email)) {
                return Result.failure(Exception("Email already exists"))
            }
            
            // Hash password and create user
            val hashedPassword = PasswordUtils.hashPassword(password)
            val user = User(
                username = username,
                email = email,
                passwordHash = hashedPassword,
                calorieGoal = calorieGoal
            )
            
            val userId = userDao.insertUser(user)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun loginUser(usernameOrEmail: String, password: String): Result<User> {
        return try {
            // Try to find user by username or email
            val user = userDao.getUserByUsername(usernameOrEmail)
                ?: userDao.getUserByEmail(usernameOrEmail)
                ?: return Result.failure(Exception("User not found"))
            
            // Verify password
            if (PasswordUtils.verifyPassword(password, user.passwordHash)) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}
