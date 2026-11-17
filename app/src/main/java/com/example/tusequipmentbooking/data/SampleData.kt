package com.example.tusequipmentbooking.data

import com.example.tusequipmentbooking.model.Category
import com.example.tusequipmentbooking.model.Equipment

val sampleCategories = listOf(
    Category(1, "Computers"),
    Category(2, "Chargers"),
    Category(3, "Cables"),
    Category(4, "Tablets")
)

val sampleEquipment = listOf(
    Equipment(1, 1, "Windows Laptop", "14 inch laptop with Windows 11 and Microsoft Office."),
    Equipment(2, 1, "MacBook", "13 inch MacBook for design and app development work."),
    Equipment(3, 2, "USB-C Laptop Charger", "65W USB-C charger that works with most laptops."),
    Equipment(4, 2, "Phone Charger", "USB-C phone charger with plug."),
    Equipment(5, 3, "HDMI Cable", "2 metre HDMI cable for connecting to a monitor or projector."),
    Equipment(6, 3, "Ethernet Cable", "3 metre network cable for a wired internet connection."),
    Equipment(7, 4, "iPad", "10 inch iPad for notes, reading and presentations."),
    Equipment(8, 4, "Android Tablet", "Android tablet for testing apps and general use.")
)
