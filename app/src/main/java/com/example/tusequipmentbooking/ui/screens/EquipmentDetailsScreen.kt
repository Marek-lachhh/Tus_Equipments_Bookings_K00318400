package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.model.Equipment

@Composable
fun EquipmentDetailsScreen(
    equipment: Equipment,
    categoryName: String,
    isBooked: Boolean,
    onBook: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(categoryName, style = MaterialTheme.typography.labelLarge)
        Text(equipment.name, style = MaterialTheme.typography.headlineMedium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Equipment ID: ${equipment.id}")
                Text("Category: $categoryName")
                Text(if (isBooked) "Status: Booked" else "Status: Available")
            }
        }
        Text("About this item", style = MaterialTheme.typography.titleLarge)
        Text(equipment.description)
        Button(
            onClick = onBook,
            enabled = !isBooked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isBooked) "Already booked" else "Book this item")
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
fun NotFoundScreen(onBack: () -> Unit) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Equipment not found")
        Button(onClick = onBack) { Text("Back") }
    }
}