package com.example.calorietracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.models.FoodItem
import com.example.calorietracker.ui.theme.NutraGreen
import com.example.calorietracker.ui.theme.TextOnGreen
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.launch
import org.json.JSONObject

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
    
    // AI Autofill states
    var mealDescription by remember { mutableStateOf("") }
    var isAiLoading by remember { mutableStateOf(false) }
    var aiErrorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ==================== AI AUTOFILL SECTION ====================
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = NutraGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "AI Autofill",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NutraGreen
                            )
                        }

                        Text(
                            text = "Describe your meal and let the AI fill in all of the nutritional details for you.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = mealDescription,
                            onValueChange = {
                                mealDescription = it
                                aiErrorMessage = ""
                            },
                            label = { Text("Describe your meal") },
                            placeholder = { Text("e.g., '1 apple', 'nasi goreng', 'chicken salad'") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 3,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        if (aiErrorMessage.isNotEmpty()) {
                            Text(
                                text = aiErrorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (mealDescription.isBlank()) return@Button
                                isAiLoading = true
                                aiErrorMessage = ""

                                scope.launch {
                                    try {
                                        // Initialize Firebase AI with Gemini model
                                        val ai = Firebase.ai(backend = GenerativeBackend.googleAI())
                                        val generativeModel = ai.generativeModel(
                                            modelName = "gemini-3-flash-preview"
                                        )

                                        val prompt = """Estimate the accurate nutrition values for: "$mealDescription". Return ONLY THE SPECIFIED JSON FORMAT: {"name":"Name","calories":0,"carbs":0,"protein":0,"fats":0}"""

                                        val response = generativeModel.generateContent(prompt)
                                        val responseText = response.text?.trim() ?: ""

                                        // Clean response - remove any markdown formatting if present
                                        val cleanJson = responseText
                                            .replace("```json", "")
                                            .replace("```", "")
                                            .trim()

                                        val jsonObject = JSONObject(cleanJson)

                                        // Update the form fields with AI response
                                        foodName = jsonObject.optString("name", mealDescription.take(30))
                                        calories = jsonObject.optInt("calories", 0).toString()
                                        carbs = jsonObject.optInt("carbs", 0).toString()
                                        protein = jsonObject.optInt("protein", 0).toString()
                                        fats = jsonObject.optInt("fats", 0).toString()

                                        isAiLoading = false
                                    } catch (e: Exception) {
                                        aiErrorMessage = "AI Error: ${e.message ?: "Unknown error"}"
                                        isAiLoading = false
                                    }
                                }
                            },
                            enabled = !isAiLoading && mealDescription.isNotBlank(),
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NutraGreen,
                                contentColor = TextOnGreen
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = TextOnGreen,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Generate")
                            }
                        }
                    }
                }

                // ==================== DIVIDER ====================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "or fill manually",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                // ==================== MANUAL INPUT SECTION ====================
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
