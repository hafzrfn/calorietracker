package com.example.calorietracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.ui.theme.TextPrimary
import com.example.calorietracker.ui.theme.TextSecondary

@Composable
fun MacroCard(
    name: String,
    icon: String,
    current: Int,
    goal: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f
    
    Column(
        modifier = modifier
            .width(100.dp)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Text(
            text = icon,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Current value
        Text(
            text = "${current}g",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        // Goal
        Text(
            text = "/ ${goal}g",
            fontSize = 12.sp,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Name
        Text(
            text = name,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
