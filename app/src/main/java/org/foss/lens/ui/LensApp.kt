// org/foss/lens/ui/LensApp.kt
package org.foss.lens.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.foss.lens.ui.screens.AssetDetailRoute
import org.foss.lens.ui.screens.ConfirmRoute
import org.foss.lens.ui.screens.HistoryRoute
import org.foss.lens.ui.screens.InventoryRoute
import org.foss.lens.ui.screens.ManualRoute
import org.foss.lens.ui.screens.ScannerRoute

object LensRoutes {
    const val SCANNER = "scanner"
    const val HISTORY = "history"
    const val INVENTORY = "inventory"
    const val MANUAL = "manual"
    const val CONFIRM = "confirm"
    const val DETAIL = "detail/{serial}"

    fun detail(serial: String) = "detail/${Uri.encode(serial)}"

    val TAB_ROUTES = setOf(SCANNER, HISTORY, INVENTORY)
}

private data class TabSpec(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabSpec(LensRoutes.SCANNER, "Escanear", Icons.Filled.QrCodeScanner),
    TabSpec(LensRoutes.INVENTORY, "Inventario", Icons.Filled.Inventory2),
    TabSpec(LensRoutes.HISTORY, "Historial", Icons.Filled.History)
)

@Composable
fun LensApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = tabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LensRoutes.SCANNER,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(LensRoutes.SCANNER) { ScannerRoute(navController) }
            composable(LensRoutes.INVENTORY) { InventoryRoute(navController) }
            composable(LensRoutes.HISTORY) { HistoryRoute() }
            composable(LensRoutes.MANUAL) { ManualRoute(navController) }
            composable(LensRoutes.CONFIRM) { ConfirmRoute(navController) }
            composable(
                LensRoutes.DETAIL,
                arguments = listOf(navArgument("serial") { type = NavType.StringType })
            ) { entry ->
                val serial = Uri.decode(entry.arguments?.getString("serial") ?: "")
                AssetDetailRoute(navController, serial)
            }
        }
    }
}
