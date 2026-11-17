package com.example.tusequipmentbooking.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val uid: String? = null,
    val email: String? = null,
    val isAdmin: Boolean = false,
    val roleReady: Boolean = false,
    val isBusy: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val mutableState = MutableStateFlow(AuthUiState(
        uid = auth.currentUser?.uid,
        email = auth.currentUser?.email
    ))
    val uiState = mutableState.asStateFlow()
    private var generation = 0

    private fun loadRole(uid: String, ticket: Int) {
        val reference = firestore.collection("users").document(uid)
        reference.get(Source.SERVER).addOnSuccessListener { document ->
            if (ticket != generation || auth.currentUser?.uid != uid) {
                return@addOnSuccessListener
            }
            if (document.exists()) {
                mutableState.update { it.copy(
                    isAdmin = document.getString("role") == "admin",
                    roleReady = true
                ) }
            } else {
                reference.set(mapOf("role" to "user"))
                    .addOnCompleteListener { task ->
                        if (ticket == generation && auth.currentUser?.uid == uid) {
                            mutableState.update { it.copy(
                                isAdmin = false, roleReady = true,
                                error = if (task.isSuccessful) null
                                else "Signed in, but your user profile could not be saved"
                            ) }
                        }
                    }
            }
        }.addOnFailureListener {
            if (ticket == generation && auth.currentUser?.uid == uid) {
                mutableState.update { it.copy(
                    isAdmin = false, roleReady = true,
                    error = "Online role check unavailable"
                ) }
            }
        }
    }

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val ticket = ++generation
        val user = firebaseAuth.currentUser
        mutableState.update { it.copy(
            uid = user?.uid, email = user?.email,
            isAdmin = false, roleReady = user == null
        ) }
        if (user != null) loadRole(user.uid, ticket)
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
                    error = if (task.isSuccessful) it.error
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
                    error = if (task.isSuccessful) it.error
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
        mutableState.value = AuthUiState(roleReady = true)
    }

    override fun onCleared() {
        ++generation
        auth.removeAuthStateListener(listener)
        super.onCleared()
    }
}