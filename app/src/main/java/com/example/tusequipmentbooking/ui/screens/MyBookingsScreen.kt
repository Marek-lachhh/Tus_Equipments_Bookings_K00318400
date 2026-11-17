package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.model.Booking
import com.example.tusequipmentbooking.model.BookingStatus
import com.example.tusequipmentbooking.ui.TusUiState

@Composable
fun MyBookingsScreen(
    state: TusUiState,
    onCancelBooking: (Int) -> Unit
) {
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text("My bookings", style = MaterialTheme.typography.headlineMedium) }
        state.message?.let { item { Text(it) } }

        if (state.bookings.isEmpty()) {
            item { Text("You have no bookings yet. Open Equipment, choose an item and tap Book this item.") }
        }

        items(state.bookings, key = { it.id }) { booking ->
            val equipment = state.findEquipment(booking.equipmentId)
            val name = equipment?.name ?: "Unknown item"
            val category = equipment?.let { state.categoryName(it.categoryId) } ?: ""
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(category.uppercase(), style = MaterialTheme.typography.labelMedium)
                    Text(name, style = MaterialTheme.typography.titleLarge)
                    Text("Days: ${booking.days}")
                    Text("Purpose: ${booking.purpose}")
                    Text("Status: ${booking.status}")
                    if (booking.status == BookingStatus.Active) {
                        OutlinedButton(onClick = { bookingToCancel = booking }) {
                            Text("Cancel booking")
                        }
                    }
                }
            }
        }
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("Cancel booking?") },
            confirmButton = {
                TextButton(onClick = {
                    onCancelBooking(booking.id)
                    bookingToCancel = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) { Text("No") }
            }
        )
    }
}