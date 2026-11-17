package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.ui.TusUiState
import com.example.tusequipmentbooking.ui.components.EquipmentCard

@Composable
fun HomeScreen(
    state: TusUiState,
    onEquipmentOpen: (Int) -> Unit,
    onBrowse: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TUS Equipment Booking", style = MaterialTheme.typography.headlineMedium)
                Text("Borrow computers, chargers, cables and tablets for your college work. Admins can also add new equipment to the catalogue.")
                Button(onClick = onBrowse) { Text("Browse all equipment") }
                Text("Featured equipment", style = MaterialTheme.typography.titleMedium)
            }
        }
        items(state.equipment.take(3), key = { it.id }) { item ->
            EquipmentCard(
                equipment = item,
                categoryName = state.categoryName(item.categoryId),
                onOpen = { onEquipmentOpen(item.id) }
            )
        }
    }
}

@Composable
fun EquipmentScreen(
    state: TusUiState,
    onEquipmentOpen: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text("All equipment", style = MaterialTheme.typography.headlineMedium) }
        state.message?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
        items(state.equipment, key = { it.id }) { item ->
            EquipmentCard(
                equipment = item,
                categoryName = state.categoryName(item.categoryId),
                onOpen = { onEquipmentOpen(item.id) }
            )
        }
    }
}