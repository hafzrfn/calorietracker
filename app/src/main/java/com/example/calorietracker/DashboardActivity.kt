package com.example.calorietracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.auth.LoginActivity
import com.example.calorietracker.components.*
import com.example.calorietracker.database.NutraDatabase
import com.example.calorietracker.database.entities.FoodEntry
import com.example.calorietracker.database.entities.User
import com.example.calorietracker.models.FoodItem
import com.example.calorietracker.repository.FoodRepository
import com.example.calorietracker.repository.UserRepository
import com.example.calorietracker.ui.theme.*
import com.example.calorietracker.utils.SessionManager
import kotlinx.coroutines.launch

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        
        if (userId == -1) {
            // Not logged in, redirect to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        
        val database = NutraDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val foodRepository = FoodRepository(database.foodEntryDao())
        
        setContent {
            CalorieTrackerTheme {
                DashboardScreen(
                    userId = userId,
                    userRepository = userRepository,
                    foodRepository = foodRepository,
                    onLogout = {
                        sessionManager.logout()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    userId: Int,
    userRepository: UserRepository,
    foodRepository: FoodRepository,
    onLogout: () -> Unit
) {
    var user by remember { mutableStateOf<User?>(null) }
    var foodEntries by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    val currentDate = System.currentTimeMillis()
    
    // Load user and food entries
    LaunchedEffect(userId) {
        scope.launch {
            user = userRepository.getUserById(userId)
            foodEntries = foodRepository.getFoodEntriesForDate(userId, currentDate)
            isLoading = false
        }
    }
    
    // Calculate totals
    val totalCalories = foodEntries.sumOf { it.calories }
    val totalCarbs = foodEntries.sumOf { it.carbs }
    val totalProtein = foodEntries.sumOf { it.protein }
    val totalFats = foodEntries.sumOf { it.fats }
    val calorieGoal = user?.calorieGoal ?: 2000
    val caloriesRemaining = (calorieGoal - totalCalories).coerceAtLeast(0)
    
    // Calculate macro goals
    val carbsGoal = (calorieGoal * 0.5 / 4).toInt()
    val proteinGoal = (calorieGoal * 0.3 / 4).toInt()
    val fatsGoal = (calorieGoal * 0.2 / 9).toInt()
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NutraGreen)
        }
        return
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NutraGreen,
                contentColor = TextOnGreen,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Food",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back!",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = user?.username ?: "User",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        
                        // Logout button
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = TextSecondary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Progress wheel
                    ProgressWheel(
                        caloriesRemaining = caloriesRemaining,
                        totalCalories = calorieGoal
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Macro cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroCard(
                            name = "Carbs",
                            icon = "🍞",
                            current = totalCarbs,
                            goal = carbsGoal,
                            color = CarbsColor
                        )
                        MacroCard(
                            name = "Protein",
                            icon = "🥩",
                            current = totalProtein,
                            goal = proteinGoal,
                            color = ProteinColor
                        )
                        MacroCard(
                            name = "Fats",
                            icon = "🥑",
                            current = totalFats,
                            goal = fatsGoal,
                            color = FatsColor
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Food list section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Eaten foods today",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (foodEntries.isEmpty()) {
                    // Empty state
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🍽️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No foods logged yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button to add your first meal",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    // Food list
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(foodEntries, key = { it.id }) { entry ->
                            FoodListItem(
                                foodItem = FoodItem(
                                    id = entry.id.toString(),
                                    name = entry.foodName,
                                    calories = entry.calories,
                                    carbs = entry.carbs,
                                    protein = entry.protein,
                                    fats = entry.fats
                                ),
                                onDelete = {
                                    scope.launch {
                                        foodRepository.deleteFoodEntry(entry.id)
                                        foodEntries = foodRepository.getFoodEntriesForDate(userId, currentDate)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add food dialog
    if (showAddDialog) {
        AddFoodDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { foodItem ->
                scope.launch {
                    foodRepository.addFoodEntry(
                        userId = userId,
                        foodName = foodItem.name,
                        calories = foodItem.calories,
                        carbs = foodItem.carbs,
                        protein = foodItem.protein,
                        fats = foodItem.fats,
                        date = currentDate
                    )
                    foodEntries = foodRepository.getFoodEntriesForDate(userId, currentDate)
                    showAddDialog = false
                }
            }
        )
    }
}

