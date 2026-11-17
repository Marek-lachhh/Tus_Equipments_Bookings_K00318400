package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.ui.TusUiState
import com.example.tusequipmentbooking.model.BookingStatus
@Composable
fun SettingsScreen(
    state: TusUiState,
    email: String?,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Settings and account", style = MaterialTheme.typography.headlineMedium)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Signed in as", style = MaterialTheme.typography.labelLarge)
                Text(email ?: "Unknown", style = MaterialTheme.typography.titleLarge)
                Text("Active bookings: ${state.bookings.count { it.status == BookingStatus.Active }}")
                Button(onClick = onLogout) { Text("Log out") }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark mode")
            Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
        }
    }
}