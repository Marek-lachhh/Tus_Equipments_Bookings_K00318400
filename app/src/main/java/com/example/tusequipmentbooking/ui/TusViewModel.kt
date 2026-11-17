package com.example.tusequipmentbooking.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tusequipmentbooking.data.local.EquipmentDatabase
import com.example.tusequipmentbooking.data.local.EquipmentRepository
import com.example.tusequipmentbooking.model.Booking
import com.example.tusequipmentbooking.model.Category
import com.example.tusequipmentbooking.model.Equipment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TusUiState(
    val equipment: List<Equipment> = emptyList(),
    val categoryNames: Map<Int, String> = emptyMap(),  // categoryId -> name (from the JOIN query)
    val categories: List<Category> = emptyList(),      // for the Add equipment form
    val bookings: List<Booking> = emptyList(),
    val bookedEquipmentIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val message: String? = null
) {
    fun categoryName(categoryId: Int): String = categoryNames[categoryId] ?: "Uncategorised"

    fun findEquipment(id: Int?): Equipment? = equipment.firstOrNull { it.id == id }

    fun isBooked(equipmentId: Int): Boolean = equipmentId in bookedEquipmentIds
}

class TusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EquipmentRepository(
        EquipmentDatabase.getInstance(application)
    )

    private val userUid = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val userBookings = userUid.flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList()) else repository.bookingsFor(uid)
    }

    private val mutableUiState = MutableStateFlow(TusUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.seedIfEmpty()
                combine(
                    repository.equipmentWithCategory,
                    repository.categories,
                    userBookings,
                    repository.bookedEquipmentIds
                ) { rows, categories, bookings, bookedIds ->
                    TusUiState(
                        equipment = rows.map { it.equipment },
                        categoryNames = rows.associate { it.equipment.categoryId to it.categoryName },
                        categories = categories,
                        bookings = bookings,
                        bookedEquipmentIds = bookedIds.toSet()
                    )
                }.collect { data ->
                    mutableUiState.update {
                        it.copy(
                            equipment = data.equipment,
                            categoryNames = data.categoryNames,
                            categories = data.categories,
                            bookings = data.bookings,
                            bookedEquipmentIds = data.bookedEquipmentIds,
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

    fun setUser(uid: String?) {
        userUid.value = uid
        mutableUiState.update { it.copy(message = null) }
    }

    fun addEquipment(name: String, categoryId: Int, description: String) {
        viewModelScope.launch {
            try {
                repository.addEquipment(name, categoryId, description)
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to add equipment") }
            }
        }
    }

    fun saveBooking(equipment: Equipment, days: Int, purpose: String) {
        val uid = userUid.value ?: return
        if (mutableUiState.value.isSaving) return
        mutableUiState.update { it.copy(isSaving = true, message = null) }

        viewModelScope.launch {
            try {
                val saved = repository.saveBooking(equipment.id, uid, days, purpose)
                val text = if (saved) {
                    "Booking saved: ${equipment.name} for $days day(s). Purpose: ${purpose.trim()}"
                } else {
                    "This item is already booked."
                }
                mutableUiState.update { it.copy(message = text) }
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to save booking") }
            } finally {
                mutableUiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun cancelBooking(bookingId: Int) {
        val uid = userUid.value ?: return
        viewModelScope.launch {
            try {
                repository.cancelBooking(bookingId, uid)
                mutableUiState.update { it.copy(message = "Booking cancelled") }
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to cancel booking") }
            }
        }
    }

    fun clearMessage() {
        mutableUiState.update { it.copy(message = null) }
    }
}