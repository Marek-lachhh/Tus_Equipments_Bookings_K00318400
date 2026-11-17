package com.example.tusequipmentbooking.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tusequipmentbooking.R
import kotlinx.coroutines.launch

// One destination used by the drawer and (if inBottomBar) the bottom bar.
private data class MenuDestination(
    val route: String,
    val label: String,
    val icon: Int,
    val inBottomBar: Boolean,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TusMenuShell(
    selectedRoute: String?,
    enabled: Boolean,
    onHome: () -> Unit,
    onEquipment: () -> Unit,
    onBookings: () -> Unit,
    onAdd: () -> Unit,
    onSettings: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val destinations = listOf(
        MenuDestination("home", "Home", R.drawable.ic_home, true, onHome),
        MenuDestination("equipment", "Equipment", R.drawable.ic_equipment, true, onEquipment),
        MenuDestination("bookings", "My Bookings", R.drawable.ic_bookings, true, onBookings),
        MenuDestination("add", "Add equipment", R.drawable.ic_add, false, onAdd),
        MenuDestination("settings", "Settings", R.drawable.ic_settings, true, onSettings)
    )

    val showBottomBar = enabled && destinations.any { it.route == selectedRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = enabled,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "TUS Equipment Booking",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                destinations.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(painterResource(item.icon), contentDescription = null) },
                        label = { Text(item.label) },
                        selected = selectedRoute == item.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()   // close first
                                item.onClick()        // then navigate
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TUS Equipment") },
                    navigationIcon = {
                        IconButton(enabled = enabled, onClick = { scope.launch { drawerState.open() } }) {
                            Icon(painterResource(R.drawable.ic_menu), contentDescription = "Open menu")
                        }
                    },
                    actions = {
                        IconButton(enabled = enabled, onClick = onSettings) {
                            Icon(painterResource(R.drawable.ic_settings), contentDescription = "Settings")
                        }
                    }
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        destinations.filter { it.inBottomBar }.forEach { item ->
                            NavigationBarItem(
                                selected = selectedRoute == item.route,
                                onClick = item.onClick,
                                icon = { Icon(painterResource(item.icon), contentDescription = null) },
                                label = { Text(item.label, maxLines = 1) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            content(padding)
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
}