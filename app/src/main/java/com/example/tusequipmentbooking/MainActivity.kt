package com.example.tusequipmentbooking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tusequipmentbooking.ui.theme.TusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TusTheme {
                TusApp()
            }
        }
    }
}
