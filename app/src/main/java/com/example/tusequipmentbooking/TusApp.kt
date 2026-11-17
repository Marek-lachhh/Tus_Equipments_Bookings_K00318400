package com.example.tusequipmentbooking

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tusequipmentbooking.ui.AuthViewModel
import com.example.tusequipmentbooking.ui.TusUiState
import com.example.tusequipmentbooking.ui.TusViewModel
import com.example.tusequipmentbooking.ui.components.TusMenuShell
import com.example.tusequipmentbooking.ui.screens.*

@Composable
fun TusApp(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val model: TusViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )
    val state by model.uiState.collectAsStateWithLifecycle()

    val authModel: AuthViewModel = viewModel()
    val authState by authModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState.uid) { model.setUser(authState.uid) }

    var showRegister by rememberSaveable { mutableStateOf(false) }
    val uid = authState.uid

    if (uid == null) {
        Surface(Modifier.fillMaxSize()) {
            if (showRegister) {
                RegisterScreen(
                    isBusy = authState.isBusy,
                    error = authState.error,
                    onRegister = authModel::register,
                    onBackToLogin = {
                        authModel.clearError()
                        showRegister = false
                    }
                )
            } else {
                LoginScreen(
                    isBusy = authState.isBusy,
                    error = authState.error,
                    onLogin = authModel::login,
                    onRegister = {
                        authModel.clearError()
                        showRegister = true
                    }
                )
            }
        }
    } else {
        key(uid) {
            MainApp(
                state = state,
                model = model,
                email = authState.email,
                isAdmin = authState.roleReady && authState.isAdmin,
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onLogout = { authModel.logout() }
            )
        }
    }
}

@Composable
private fun MainApp(
    state: TusUiState,
    model: TusViewModel,
    email: String?,
    isAdmin: Boolean,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun openTab(route: String) {
        model.clearMessage()
        navController.navigate(route) {
            popUpTo("home") { inclusive = false }
            launchSingleTop = true
        }
    }

    TusMenuShell(
        selectedRoute = currentRoute,
        enabled = !state.isLoading && state.loadError == null,
        isAdmin = isAdmin,
        onHome = { openTab("home") },
        onEquipment = { openTab("equipment") },
        onBookings = { openTab("bookings") },
        onAdd = { openTab("add") },
        onSettings = { openTab("settings") }
    ) { padding ->
        if (state.isLoading) {
            Column(Modifier.padding(padding).padding(16.dp)) {
                CircularProgressIndicator()
                Text("Loading equipment...")
            }
        } else if (state.loadError != null) {
            Text(
                text = "Database error: ${state.loadError}",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") {
                    HomeScreen(
                        state = state,
                        onEquipmentOpen = { id -> navController.navigate("details/$id") },
                        onBrowse = { openTab("equipment") }
                    )
                }
                composable("equipment") {
                    EquipmentScreen(
                        state = state,
                        onEquipmentOpen = { id -> navController.navigate("details/$id") }
                    )
                }
                composable(
                    route = "details/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { entry ->
                    val equipment = state.findEquipment(entry.arguments?.getInt("id"))
                    if (equipment == null) {
                        NotFoundScreen(onBack = { navController.popBackStack() })
                    } else {
                        EquipmentDetailsScreen(
                            equipment = equipment,
                            categoryName = state.categoryName(equipment.categoryId),
                            isBooked = state.isBooked(equipment.id),
                            onBook = {
                                model.clearMessage()
                                navController.navigate("book/${equipment.id}")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(
                    route = "book/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { entry ->
                    val equipment = state.findEquipment(entry.arguments?.getInt("id"))
                    if (equipment == null) {
                        NotFoundScreen(onBack = { navController.popBackStack() })
                    } else {
                        MakeBookingScreen(
                            equipment = equipment,
                            categoryName = state.categoryName(equipment.categoryId),
                            isBooked = state.isBooked(equipment.id),
                            isSaving = state.isSaving,
                            message = state.message,
                            onSave = { days, purpose -> model.saveBooking(equipment, days, purpose) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("bookings") {
                    MyBookingsScreen(
                        state = state,
                        onCancelBooking = { id -> model.cancelBooking(id) }
                    )
                }
                composable("add") {
                    if (isAdmin) {
                        AddEquipmentScreen(
                            categories = state.categories,
                            onSave = { name, categoryId, description ->
                                model.addEquipment(name, categoryId, description)
                                openTab("equipment")
                            }
                        )
                    } else {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Only admins can add equipment.")
                            Button(onClick = { navController.popBackStack() }) { Text("Back") }
                        }
                    }
                }
                composable("settings") {
                    SettingsScreen(
                        state = state,
                        email = email,
                        isAdmin = isAdmin,
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}