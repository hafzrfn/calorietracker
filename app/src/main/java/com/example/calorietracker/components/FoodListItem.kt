package com.example.calorietracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.models.FoodItem
import com.example.calorietracker.ui.theme.*

@Composable
fun FoodListItem(
    foodItem: FoodItem,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food icon and info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = NutraGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getFoodIcon(foodItem.name),
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Food details
                Column {
                    Text(
                        text = foodItem.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Macros
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroChip("C: ${foodItem.carbs}g", CarbsColor)
                        MacroChip("P: ${foodItem.protein}g", ProteinColor)
                        MacroChip("F: ${foodItem.fats}g", FatsColor)
                    }
                }
            }
            
            // Calories and delete button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${foodItem.calories}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Food Item") },
            text = { Text("Are you sure you want to delete ${foodItem.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MacroChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getFoodIcon(foodName: String): String {
    return when {
        foodName.contains("chicken", ignoreCase = true) -> "🍗"
        foodName.contains("salad", ignoreCase = true) -> "🥗"
        foodName.contains("oatmeal", ignoreCase = true) -> "🥣"
        foodName.contains("berries", ignoreCase = true) -> "🫐"
        foodName.contains("shake", ignoreCase = true) -> "🥤"
        foodName.contains("protein", ignoreCase = true) -> "🥤"
        foodName.contains("egg", ignoreCase = true) -> "🥚"
        foodName.contains("rice", ignoreCase = true) -> "🍚"
        foodName.contains("fish", ignoreCase = true) -> "🐟"
        foodName.contains("beef", ignoreCase = true) -> "🥩"
        foodName.contains("fruit", ignoreCase = true) -> "🍎"
        foodName.contains("vegetable", ignoreCase = true) -> "🥦"
        else -> "🍽️"
    }
}
