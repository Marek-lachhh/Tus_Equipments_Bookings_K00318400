package com.example.tusequipmentbooking.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tusequipmentbooking.model.Booking
import com.example.tusequipmentbooking.model.Category
import com.example.tusequipmentbooking.model.Equipment

@Database(
    entities = [Category::class, Equipment::class, Booking::class],
    version = 4,
    exportSchema = true
)
abstract class EquipmentDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao

    companion object {
        @Volatile private var instance: EquipmentDatabase? = null

        fun getInstance(context: Context): EquipmentDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EquipmentDatabase::class.java,
                    "tus_equipment.db"
                ).fallbackToDestructiveMigration(true).build().also { instance = it }
            }
    }
}