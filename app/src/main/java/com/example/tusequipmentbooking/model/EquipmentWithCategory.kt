package com.example.tusequipmentbooking.model

import androidx.room.Embedded

data class EquipmentWithCategory(
    @Embedded val equipment: Equipment,
    val categoryName: String
)