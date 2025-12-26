package com.example.calorietracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.auth.LoginActivity
import com.example.calorietracker.ui.theme.CalorieTrackerTheme
import com.example.calorietracker.ui.theme.NutraGreen
import com.example.calorietracker.ui.theme.TextPrimary
import com.example.calorietracker.utils.SessionManager
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CalorieTrackerTheme {
                SplashScreen {
                    navigateToNextScreen()
                }
            }
        }
    }
    
    private fun navigateToNextScreen() {
        val sessionManager = SessionManager(this)
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, DashboardActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ), label = "scale"
    )
    
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onTimeout()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale)
        ) {
            // Nutra logo image
            Image(
                painter = painterResource(id = R.drawable.nutra_logo),
                contentDescription = "Nutra Logo",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}
