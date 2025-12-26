package com.example.calorietracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.models.FoodItem
import com.example.calorietracker.ui.theme.NutraGreen
import com.example.calorietracker.ui.theme.TextOnGreen

@Composable
fun AddFoodDialog(
    onDismiss: () -> Unit,
    onAdd: (FoodItem) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Food",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Food name
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { 
                        foodName = it
                        errorMessage = ""
                    },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Calories
                OutlinedTextField(
                    value = calories,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            calories = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Calories (kcal)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                // Macros row
                Text(
                    text = "Macronutrients (grams)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Carbs
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                carbs = it
                                errorMessage = ""
                            }
                        },
                        label = { Text("Carbs") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    // Protein
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                protein = it
                                errorMessage = ""
                            }
                        },
                        label = { Text("Protein") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    // Fats
                    OutlinedTextField(
                        value = fats,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                fats = it
                                errorMessage = ""
                            }
                        },
                        label = { Text("Fats") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        foodName.isBlank() -> {
                            errorMessage = "Please enter a food name"
                        }
                        calories.toIntOrNull() == null || calories.toInt() <= 0 -> {
                            errorMessage = "Please enter valid calories"
                        }
                        carbs.toIntOrNull() == null -> {
                            errorMessage = "Please enter valid carbs"
                        }
                        protein.toIntOrNull() == null -> {
                            errorMessage = "Please enter valid protein"
                        }
                        fats.toIntOrNull() == null -> {
                            errorMessage = "Please enter valid fats"
                        }
                        else -> {
                            val foodItem = FoodItem(
                                name = foodName.trim(),
                                calories = calories.toInt(),
                                carbs = carbs.toInt(),
                                protein = protein.toInt(),
                                fats = fats.toInt()
                            )
                            onAdd(foodItem)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NutraGreen,
                    contentColor = TextOnGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
