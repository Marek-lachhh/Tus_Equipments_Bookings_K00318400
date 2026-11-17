package com.example.tusequipmentbooking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.R

fun equipmentImageRes(categoryId: Int): Int = when (categoryId) {
    1 -> R.drawable.ic_cat_computer
    2 -> R.drawable.ic_cat_charger
    3 -> R.drawable.ic_cat_cable
    4 -> R.drawable.ic_cat_tablet
    else -> R.drawable.ic_equipment
}

@Composable
fun EquipmentImage(
    categoryId: Int,
    contentDescription: String,
    size: Dp = 56.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(equipmentImageRes(categoryId)),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}