package com.example.calorietracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.ui.theme.*
import com.example.calorietracker.utils.CaloriePreferences

class CalorieInputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CalorieTrackerTheme {
                CalorieInputScreen(
                    onSubmit = { calorieGoal ->
                        val prefs = CaloriePreferences(this)
                        prefs.setCalorieGoal(calorieGoal)
                        prefs.setFirstLaunchComplete()
                        
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun CalorieInputScreen(onSubmit: (Int) -> Unit) {
    var calorieInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Text(
                text = "Goal Setting",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Prompt text
            Text(
                text = "What's your desired",
                fontSize = 18.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "calorie intake?",
                fontSize = 18.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Calorie input display
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (calorieInput.isEmpty()) "0" else calorieInput,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "kcal",
                    fontSize = 20.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Number pad
            NumberPad(
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
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
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
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Set Goal →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (String) -> Unit,
    onBackspace: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("1", onNumberClick)
            NumberButton("2", onNumberClick)
            NumberButton("3", onNumberClick)
        }
        
        // Row 2: 4, 5, 6
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("4", onNumberClick)
            NumberButton("5", onNumberClick)
            NumberButton("6", onNumberClick)
        }
        
        // Row 3: 7, 8, 9
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("7", onNumberClick)
            NumberButton("8", onNumberClick)
            NumberButton("9", onNumberClick)
        }
        
        // Row 4: 0, backspace
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.size(80.dp))
            NumberButton("0", onNumberClick)
            IconButton(
                onClick = onBackspace,
                modifier = Modifier.size(80.dp)
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
fun NumberButton(number: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(number) },
        modifier = Modifier.size(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBackground,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
