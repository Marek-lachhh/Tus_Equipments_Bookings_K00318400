package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.ui.TusUiState

@Composable
fun ProfileScreen(state: TusUiState) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TUS Equipment Booking", style = MaterialTheme.typography.titleLarge)
                Text("Categories: ${state.categories.size}")
                Text("Equipment items: ${state.equipment.size}")
                Text("Active bookings: ${state.bookings.count { it.status == "Active" }}")
            }
        }
    }
}