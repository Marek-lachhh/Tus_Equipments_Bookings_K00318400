package com.example.tusequipmentbooking

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tusequipmentbooking.ui.TusViewModel
import com.example.tusequipmentbooking.ui.screens.AddEquipmentScreen
import com.example.tusequipmentbooking.ui.screens.EquipmentDetailsScreen
import com.example.tusequipmentbooking.ui.screens.EquipmentScreen
import com.example.tusequipmentbooking.ui.screens.HomeScreen
import com.example.tusequipmentbooking.ui.screens.MakeBookingScreen
import com.example.tusequipmentbooking.ui.screens.MyBookingsScreen
import com.example.tusequipmentbooking.ui.screens.NotFoundScreen
import com.example.tusequipmentbooking.ui.screens.ProfileScreen

@Composable
fun TusApp() {
    val application = LocalContext.current.applicationContext as Application
    val model: TusViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )
    val state by model.uiState.collectAsStateWithLifecycle()

    var screen by rememberSaveable { mutableStateOf("home") }
    var backScreen by rememberSaveable { mutableStateOf("home") }
    var selectedId by rememberSaveable { mutableStateOf(0) }

    fun openDetails(id: Int) {
        selectedId = id
        backScreen = screen
        screen = "details"
    }

    BackHandler(enabled = screen != "home") {
        screen = when (screen) {
            "details" -> backScreen
            "book" -> "details"
            else -> "home"
        }
    }

    Scaffold(
        bottomBar = { BottomBar(current = screen, onSelect = { screen = it }) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (state.isLoading) {
                Column(Modifier.padding(16.dp)) {
                    CircularProgressIndicator()
                    Text("Loading equipment...")
                }
            } else if (state.loadError != null) {
                Text(
                    text = "Database error: ${state.loadError}",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                when (screen) {
                    "home" -> HomeScreen(
                        state = state,
                        onEquipmentOpen = { id -> openDetails(id) },
                        onBrowse = { screen = "equipment" }
                    )
                    "equipment" -> EquipmentScreen(
                        state = state,
                        onEquipmentOpen = { id -> openDetails(id) }
                    )
                    "details" -> {
                        val equipment = state.findEquipment(selectedId)
                        if (equipment == null) {
                            NotFoundScreen(onBack = { screen = backScreen })
                        } else {
                            EquipmentDetailsScreen(
                                equipment = equipment,
                                categoryName = state.categoryName(equipment.categoryId),
                                isBooked = state.isBooked(equipment.id),
                                onBook = {
                                    model.clearMessage()
                                    screen = "book"
                                },
                                onBack = { screen = backScreen }
                            )
                        }
                    }
                    "book" -> {
                        val equipment = state.findEquipment(selectedId)
                        if (equipment == null) {
                            NotFoundScreen(onBack = { screen = "equipment" })
                        } else {
                            MakeBookingScreen(
                                equipment = equipment,
                                categoryName = state.categoryName(equipment.categoryId),
                                isBooked = state.isBooked(equipment.id),
                                isSaving = state.isSaving,
                                message = state.message,
                                onSave = { days, purpose -> model.saveBooking(equipment, days, purpose) },
                                onBack = { screen = "details" }
                            )
                        }
                    }
                    "bookings" -> MyBookingsScreen(
                        state = state,
                        onCancelBooking = { id -> model.cancelBooking(id) }
                    )
                    "add" -> AddEquipmentScreen(
                        categories = state.categories,
                        onSave = { name, categoryId, description ->
                            model.addEquipment(name, categoryId, description)
                            screen = "equipment"
                        }
                    )
                    "profile" -> ProfileScreen(state = state)
                }
            }
        }
    }
}

@Composable
fun BottomBar(current: String, onSelect: (String) -> Unit) {
    val tabs = listOf(
        "home" to "Home",
        "equipment" to "Equipment",
        "bookings" to "Bookings",
        "add" to "Add",
        "profile" to "Profile"
    )

    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { (route, label) ->
                TextButton(
                    onClick = { onSelect(route) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    Text(
                        label,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (current == route) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}