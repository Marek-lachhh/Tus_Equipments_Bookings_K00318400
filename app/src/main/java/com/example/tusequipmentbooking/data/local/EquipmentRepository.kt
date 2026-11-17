package com.example.tusequipmentbooking.data.local

import androidx.room.withTransaction
import com.example.tusequipmentbooking.data.sampleCategories
import com.example.tusequipmentbooking.data.sampleEquipment
import com.example.tusequipmentbooking.model.Booking
import com.example.tusequipmentbooking.model.Equipment

class EquipmentRepository(private val database: EquipmentDatabase) {
    private val dao = database.equipmentDao()

    val equipment = dao.observeEquipment()
    val categories = dao.observeCategories()
    val bookings = dao.observeBookings()

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


    suspend fun saveBooking(equipmentId: Int, days: Int, purpose: String): Boolean {
        if (dao.activeBookingCount(equipmentId) > 0) return false
        dao.insertBooking(Booking(0, equipmentId, days, purpose.trim(), "Active"))
        return true
    }

    suspend fun cancelBooking(bookingId: Int) {
        dao.cancelBooking(bookingId)
    }
}