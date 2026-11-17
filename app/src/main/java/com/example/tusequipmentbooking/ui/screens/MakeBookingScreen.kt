package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.model.Equipment

@Composable
fun MakeBookingScreen(
    equipment: Equipment,
    categoryName: String,
    isBooked: Boolean,
    isSaving: Boolean,
    message: String?,
    onSave: (Int, String) -> Unit,
    onBack: () -> Unit
) {

    var days by rememberSaveable { mutableIntStateOf(1) }
    var purpose by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Make a booking", style = MaterialTheme.typography.headlineMedium)
        Text("$categoryName: ${equipment.name}", style = MaterialTheme.typography.titleMedium)

        // Shows "Booking saved…" or "already booked" after tapping Confirm.
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        if (isBooked) {
            Text("This item is booked.", color = MaterialTheme.colorScheme.error)
        } else {
            Text("Days (1 to 5)")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = { days-- }, enabled = days > 1) { Text("-") }
                Text("$days", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = { days++ }, enabled = days < 5) { Text("+") }
            }

            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it; error = false },
                label = { Text("Purpose") },
                isError = error,
                supportingText = { if (error) Text("Please enter a purpose.") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (purpose.isBlank()) error = true
                    else onSave(days, purpose)
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Saving…" else "Confirm booking")
            }
        }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}