package com.example.tusequipmentbooking.data.local

import androidx.room.withTransaction
import com.example.tusequipmentbooking.data.sampleCategories
import com.example.tusequipmentbooking.data.sampleEquipment
import com.example.tusequipmentbooking.model.Booking
import com.example.tusequipmentbooking.model.BookingStatus
import com.example.tusequipmentbooking.model.Equipment
import kotlinx.coroutines.flow.Flow

class EquipmentRepository(private val database: EquipmentDatabase) {
    private val dao = database.equipmentDao()

    val equipmentWithCategory = dao.observeEquipmentWithCategory()
    val categories = dao.observeCategories()
    val bookedEquipmentIds = dao.observeBookedEquipmentIds()

    fun bookingsFor(userUid: String): Flow<List<Booking>> = dao.observeBookingsForUser(userUid)

    suspend fun seedIfEmpty() {
        database.withTransaction {
            if (dao.categoryCount() == 0 && dao.equipmentCount() == 0) {
                dao.insertCategories(sampleCategories)
                dao.insertEquipmentList(sampleEquipment)
            }
        }
    }

    suspend fun addEquipment(name: String, categoryId: Int, description: String) {
        dao.insertEquipment(Equipment(0, categoryId, name.trim(), description.trim()))
    }

    // Returns false if the item already has an active booking (by any user).
    suspend fun saveBooking(equipmentId: Int, userUid: String, days: Int, purpose: String): Boolean {
        if (dao.activeBookingCount(equipmentId) > 0) return false
        dao.insertBooking(Booking(0, equipmentId, userUid, days, purpose.trim(), BookingStatus.Active))
        return true
    }

    suspend fun cancelBooking(bookingId: Int, userUid: String) {
        dao.cancelBooking(bookingId, userUid)
    }
}