package com.example.calorietracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.database.NutraDatabase
import com.example.calorietracker.database.entities.User
import com.example.calorietracker.repository.UserRepository
import com.example.calorietracker.ui.theme.*
import com.example.calorietracker.utils.SessionManager
import kotlinx.coroutines.launch

class CalorieSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        val database = NutraDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        
        setContent {
            CalorieTrackerTheme {
                var currentCalorieGoal by remember { mutableStateOf<Int?>(null) }
                
                LaunchedEffect(userId) {
                    val user = userRepository.getUserById(userId)
                    currentCalorieGoal = user?.calorieGoal ?: 2000
                }
                
                if (currentCalorieGoal != null) {
                    CalorieSettingsScreen(
                        initialCalories = currentCalorieGoal!!,
                        onSubmit = { calorieGoal ->
                            kotlinx.coroutines.MainScope().launch {
                                val user = userRepository.getUserById(userId)
                                if (user != null) {
                                    userRepository.updateUser(user.copy(calorieGoal = calorieGoal))
                                }
                                // Restart Dashboard to refresh data
                                val intent = Intent(this@CalorieSettingsActivity, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        },
                        onBack = {
                            finish()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NutraGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun CalorieSettingsScreen(
    initialCalories: Int,
    onSubmit: (Int) -> Unit,
    onBack: () -> Unit
) {
    var calorieInput by remember { mutableStateOf(initialCalories.toString()) }
    var errorMessage by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                
                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.size(48.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Main heading
                Row {
                    Text(
                        text = "Update your ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "daily target",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = NutraGreen
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Calorie display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (calorieInput.isEmpty()) "0" else formatCaloriesSettings(calorieInput),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "kcal",
                        fontSize = 20.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recommended info box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = LightBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(NutraGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "i",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NutraGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row {
                                Text(
                                    text = "Recommended: ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "2,200 - 2,800",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Based on the average activity level of a human adult",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Submit button
                Button(
                    onClick = {
                        val calories = calorieInput.toIntOrNull()
                        when {
                            calories == null || calories == 0 -> {
                                errorMessage = "Please enter a valid calorie goal"
                            }
                            calories < 1000 -> {
                                errorMessage = "Calorie goal should be at least 1000 kcal"
                            }
                            calories > 10000 -> {
                                errorMessage = "Calorie goal seems too high"
                            }
                            else -> {
                                onSubmit(calories)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NutraGreen,
                        contentColor = TextOnGreen
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Update Goal  →",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Number pad
                SettingsNumberPad(
                    onNumberClick = { number ->
                        if (calorieInput.length < 5) {
                            calorieInput += number
                            errorMessage = ""
                        }
                    },
                    onBackspace = {
                        if (calorieInput.isNotEmpty()) {
                            calorieInput = calorieInput.dropLast(1)
                            errorMessage = ""
                        }
                    },
                    onClear = {
                        calorieInput = ""
                        errorMessage = ""
                    }
                )
            }
        }
    }
}

private fun formatCaloriesSettings(value: String): String {
    val number = value.toIntOrNull() ?: return value
    return String.format("%,d", number)
}

@Composable
fun SettingsNumberPad(
    onNumberClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsNumberButton("1", onNumberClick)
            SettingsNumberButton("2", onNumberClick)
            SettingsNumberButton("3", onNumberClick)
        }
        
        // Row 2: 4, 5, 6
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsNumberButton("4", onNumberClick)
            SettingsNumberButton("5", onNumberClick)
            SettingsNumberButton("6", onNumberClick)
        }
        
        // Row 3: 7, 8, 9
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsNumberButton("7", onNumberClick)
            SettingsNumberButton("8", onNumberClick)
            SettingsNumberButton("9", onNumberClick)
        }
        
        // Row 4: clear, 0, backspace
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(64.dp)
            ) {
                Text(
                    text = "C",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
            SettingsNumberButton("0", onNumberClick)
            IconButton(
                onClick = onBackspace,
                modifier = Modifier.size(64.dp)
            ) {
                Text(
                    text = "⌫",
                    fontSize = 24.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun SettingsNumberButton(number: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable { onClick(number) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
