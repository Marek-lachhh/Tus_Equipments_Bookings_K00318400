package com.example.tusequipmentbooking.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookings",
    foreignKeys = [ForeignKey(
        entity = Equipment::class,
        parentColumns = ["id"],
        childColumns = ["equipmentId"]
    )],
    indices = [Index(value = ["equipmentId"])]
)
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val equipmentId: Int,
    val days: Int,
    val purpose: String,
    val status: String // "Active" or "Cancelled"
)