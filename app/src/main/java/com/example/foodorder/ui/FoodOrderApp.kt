package com.example.foodorder.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodorder.ui.screens.CartScreen
import com.example.foodorder.ui.screens.CheckoutScreen
import com.example.foodorder.ui.screens.DetailScreen
import com.example.foodorder.ui.screens.HomeScreen
import com.example.foodorder.ui.screens.SuccessScreen
import com.example.foodorder.ui.screens.ThemeScreen
import com.example.foodorder.ui.theme.ThemeMode
import com.example.foodorder.viewmodel.FoodViewModel

object Routes {
    const val HOME = "home"
    const val CART = "cart"
    const val THEME = "theme"
    const val DETAIL = "detail/{foodId}"
    const val CHECKOUT = "checkout"
    const val SUCCESS = "success/{code}"

    fun detail(foodId: Int) = "detail/$foodId"
    fun success(code: String) = "success/$code"
}

private data class TabDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FoodOrderApp(
    vm: FoodViewModel,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = remember {
        listOf(
            TabDestination(Routes.HOME, "Beranda", Icons.Outlined.Home, Icons.Filled.Home),
            TabDestination(Routes.CART, "Keranjang", Icons.Outlined.ShoppingCart, Icons.Filled.ShoppingCart),
            TabDestination(Routes.THEME, "Tema", Icons.Outlined.Palette, Icons.Filled.Palette)
        )
    }

    // Navigasi bawah / rail hanya tampil di 3 halaman utama
    val showNav = tabs.any { it.route == currentRoute }
    // Adaptive layout: HP = NavigationBar di bawah, layar lebar = NavigationRail di kiri
    val useRail = rememberWidthClass() != WidthClass.Compact

    val onTabSelected: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Row(Modifier.fillMaxSize()) {
        if (showNav && useRail) {
            NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                Spacer(Modifier.height(12.dp))
                tabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    NavigationRailItem(
                        selected = selected,
                        onClick = { onTabSelected(tab.route) },
                        icon = { TabIcon(tab, selected, vm.totalItems) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }

        Scaffold(
            modifier = Modifier.weight(1f),
            // Insets atas diurus TopAppBar di tiap screen; Scaffold ini hanya mengurus bagian bawah.
            contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showNav && !useRail) {
                    NavigationBar {
                        tabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { onTabSelected(tab.route) },
                                icon = { TabIcon(tab, selected, vm.totalItems) },
                                label = { Text(tab.label) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(tween(250)) + slideInHorizontally(tween(250), initialOffsetX = { it / 10 })
                },
                exitTransition = { fadeOut(tween(150)) },
                popEnterTransition = { fadeIn(tween(250)) },
                popExitTransition = {
                    fadeOut(tween(150)) + slideOutHorizontally(tween(250), targetOffsetX = { it / 10 })
                }
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        vm = vm,
                        snackbarHostState = snackbarHostState,
                        onFoodClick = { navController.navigate(Routes.detail(it)) },
                        onGoToCart = { onTabSelected(Routes.CART) }
                    )
                }

                composable(Routes.CART) {
                    CartScreen(
                        vm = vm,
                        snackbarHostState = snackbarHostState,
                        onCheckout = { navController.navigate(Routes.CHECKOUT) },
                        onBrowse = { onTabSelected(Routes.HOME) }
                    )
                }

                composable(Routes.THEME) {
                    ThemeScreen(
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange,
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = onDynamicColorChange
                    )
                }

                composable(
                    route = Routes.DETAIL,
                    arguments = listOf(navArgument("foodId") { type = NavType.IntType })
                ) { entry ->
                    DetailScreen(
                        foodId = entry.arguments?.getInt("foodId") ?: -1,
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onGoToCart = {
                            navController.navigate(Routes.CART) {
                                popUpTo(Routes.HOME)
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Routes.CHECKOUT) {
                    CheckoutScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onOrderPlaced = { code ->
                            navController.navigate(Routes.success(code)) {
                                popUpTo(Routes.HOME)
                            }
                        }
                    )
                }

                composable(
                    route = Routes.SUCCESS,
                    arguments = listOf(navArgument("code") { type = NavType.StringType })
                ) { entry ->
                    SuccessScreen(
                        orderCode = entry.arguments?.getString("code").orEmpty(),
                        onDone = { navController.popBackStack(Routes.HOME, inclusive = false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabIcon(tab: TabDestination, selected: Boolean, cartCount: Int) {
    val icon = if (selected) tab.selectedIcon else tab.icon
    if (tab.route == Routes.CART && cartCount > 0) {
        // Badge jumlah item di ikon keranjang
        BadgedBox(badge = { Badge { Text("$cartCount") } }) {
            Icon(icon, contentDescription = null)
        }
    } else {
        Icon(icon, contentDescription = null)
    }
}
