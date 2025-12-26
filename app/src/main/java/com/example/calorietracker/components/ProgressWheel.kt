package com.example.calorietracker.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.ui.theme.NutraGreen
import com.example.calorietracker.ui.theme.TextPrimary
import com.example.calorietracker.ui.theme.TextSecondary

@Composable
fun ProgressWheel(
    caloriesRemaining: Int,
    totalCalories: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalCalories > 0) {
        ((totalCalories - caloriesRemaining).toFloat() / totalCalories.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ), label = "progress"
    )
    
    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            
            // Background arc
            drawArc(
                color = Color(0xFFE8E8E8),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2)
            )
            
            // Progress arc with gradient
            if (animatedProgress > 0) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            NutraGreen,
                            NutraGreen.copy(alpha = 0.7f),
                            NutraGreen
                        )
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(
                        (size.width - radius * 2) / 2,
                        (size.height - radius * 2) / 2
                    ),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
        
        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calories Left",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = caloriesRemaining.toString(),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "/ $totalCalories kcal",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}
