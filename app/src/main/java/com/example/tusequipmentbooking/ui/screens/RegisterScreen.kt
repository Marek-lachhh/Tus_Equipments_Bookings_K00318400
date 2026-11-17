package com.example.tusequipmentbooking.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    isBusy: Boolean,
    error: String?,
    onRegister: (String, String, String, Boolean) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var asAdmin by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState())
        .padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Text("Registration needs an internet connection.")
        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true, enabled = !isBusy,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Password (min 6 characters)") }, singleLine = true, enabled = !isBusy,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") }, singleLine = true, enabled = !isBusy,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Text("Account type", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !asAdmin, onClick = { asAdmin = false },
                enabled = !isBusy, label = { Text("User") })
            FilterChip(selected = asAdmin, onClick = { asAdmin = true },
                enabled = !isBusy, label = { Text("Admin") })
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(enabled = !isBusy,
            onClick = { onRegister(email, password, confirmPassword, asAdmin) }) {
            Text(if (isBusy) "Creating account…" else "Register")
        }
        TextButton(enabled = !isBusy, onClick = onBackToLogin) {
            Text("Already have an account? Log in")
        }
    }
}