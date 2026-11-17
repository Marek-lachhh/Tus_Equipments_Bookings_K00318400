package com.example.tusequipmentbooking.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tusequipmentbooking.data.local.EquipmentDatabase
import com.example.tusequipmentbooking.data.local.EquipmentRepository
import com.example.tusequipmentbooking.model.Booking
import com.example.tusequipmentbooking.model.Category
import com.example.tusequipmentbooking.model.Equipment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TusUiState(
    val equipment: List<Equipment> = emptyList(),
    val categories: List<Category> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val message: String? = null
) {
    fun categoryName(categoryId: Int): String =
        categories.firstOrNull { it.id == categoryId }?.name ?: "Uncategorised"

    fun findEquipment(id: Int?): Equipment? = equipment.firstOrNull { it.id == id }

    fun isBooked(equipmentId: Int): Boolean =
        bookings.any { it.equipmentId == equipmentId && it.status == "Active" }
}

class TusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EquipmentRepository(
        EquipmentDatabase.getInstance(application)
    )

    private val mutableUiState = MutableStateFlow(TusUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.seedIfEmpty()
                combine(
                    repository.equipment,
                    repository.categories,
                    repository.bookings
                ) { equipment, categories, bookings ->
                    Triple(equipment, categories, bookings)
                }.collect { (equipment, categories, bookings) ->
                    mutableUiState.update {
                        it.copy(
                            equipment = equipment,
                            categories = categories,
                            bookings = bookings,
                            isLoading = false
                        )
                    }
                }
            } catch (error: Exception) {
                mutableUiState.update {
                    it.copy(isLoading = false, loadError = error.message ?: "Unable to load equipment")
                }
            }
        }
    }

    fun addEquipment(name: String, categoryId: Int, description: String) {
        viewModelScope.launch {
            repository.addEquipment(name, categoryId, description)
        }
    }

    fun saveBooking(equipment: Equipment, days: Int, purpose: String) {
        if (mutableUiState.value.isSaving) return // stops double taps
        mutableUiState.update { it.copy(isSaving = true, message = null) }

        viewModelScope.launch {
            val saved = repository.saveBooking(equipment.id, days, purpose)
            val text = if (saved) {
                "Booking saved: ${equipment.name} for $days day(s). Purpose: ${purpose.trim()}"
            } else {
                "This item is already booked."
            }
            mutableUiState.update { it.copy(isSaving = false, message = text) }
        }
    }

    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
            mutableUiState.update { it.copy(message = "Booking cancelled") }
        }
    }

    fun clearMessage() {
        mutableUiState.update { it.copy(message = null) }
    }
}