package com.example.tusequipmentbooking.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val ADMIN_EMAILS = setOf(
    "admin@tus.ie"
)

private fun isAdminEmail(email: String?): Boolean =
    email != null && email.lowercase() in ADMIN_EMAILS

data class AuthUiState(
    val uid: String? = null,
    val email: String? = null,
    val isAdmin: Boolean = false,
    val isBusy: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val mutableState = MutableStateFlow(AuthUiState(
        uid = auth.currentUser?.uid,
        email = auth.currentUser?.email,
        isAdmin = isAdminEmail(auth.currentUser?.email)
    ))
    val uiState = mutableState.asStateFlow()

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        mutableState.update { it.copy(
            uid = user?.uid,
            email = user?.email,
            isAdmin = isAdminEmail(user?.email)
        ) }
    }
    init { auth.addAuthStateListener(listener) }

    fun login(email: String, password: String) {
        if (mutableState.value.isBusy) return
        if (email.isBlank() || password.isBlank()) {
            mutableState.update { it.copy(error = "Enter email and password") }
            return
        }
        mutableState.update { it.copy(isBusy = true, error = null) }
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                mutableState.update { it.copy(
                    isBusy = false,
                    error = if (task.isSuccessful) null
                    else "Login failed. Check credentials and connection."
                ) }
            }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        if (mutableState.value.isBusy) return
        val problem = when {
            email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                "Fill in all fields"
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Enter a valid email address"
            password.length < 6 -> "Password must be at least 6 characters"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
        if (problem != null) {
            mutableState.update { it.copy(error = problem) }
            return
        }
        mutableState.update { it.copy(isBusy = true, error = null) }
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                mutableState.update { it.copy(
                    isBusy = false,
                    error = if (task.isSuccessful) null
                    else "Registration failed. The email may already be used, or check your connection."
                ) }
            }
    }

    fun clearError() {
        mutableState.update { it.copy(error = null) }
    }

    fun logout() {
        if (mutableState.value.isBusy) return
        auth.signOut()
        mutableState.value = AuthUiState()
    }

    override fun onCleared() {
        auth.removeAuthStateListener(listener)
        super.onCleared()
    }
}