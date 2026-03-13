package com.example.sakafo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sakafo.data.FoodData
import com.example.sakafo.data.preferences.UserPreferences
import com.example.sakafo.data.repository.AuthRepository
import com.example.sakafo.ui.screen.auth.ForgotPasswordScreen
import com.example.sakafo.ui.screen.auth.LoginScreenAuth
import com.example.sakafo.ui.screen.auth.SignUpScreen
import com.example.sakafo.ui.screen.auth.VerifyEmailScreen
import com.example.sakafo.ui.screen.cart.CartManager
import com.example.sakafo.ui.screen.cart.CartScreen
import com.example.sakafo.ui.screen.cart.CheckoutScreen          // ✅ nouveau
import com.example.sakafo.ui.screen.details.DishDetailScreen
import com.example.sakafo.ui.screen.home.FoodDeliveryScreen
import com.example.sakafo.ui.screen.notification.NotificationScreen
import com.example.sakafo.ui.screen.orders.OrdersScreen
import com.example.sakafo.ui.screen.splashscreen.LogoScreen
import com.example.sakafo.ui.screen.splashscreen.SplashScreen
import com.example.sakafo.viewmodel.AuthViewModel
import com.example.sakafo.viewmodel.AuthViewModelFactory

// ── Routes ────────────────────────────────────────────────────────────────────
sealed class AuthScreen(val route: String) {

    object Logo           : AuthScreen("logo")
    object Splash         : AuthScreen("splash")
    object Login          : AuthScreen("login")
    object SignUp         : AuthScreen("signup")
    object VerifyEmail    : AuthScreen("verify_email")
    object ForgotPassword : AuthScreen("forgot_password")
    object Home           : AuthScreen("home")
    object Notification   : AuthScreen("notifications")
    object Cart           : AuthScreen("cart")
    object Checkout       : AuthScreen("checkout")               // ✅ nouveau
    object Orders         : AuthScreen("orders")
    object DishDetail     : AuthScreen("dish_detail/{dishId}") {
        fun createRoute(dishId: String) = "dish_detail/$dishId"
    }
}

// ── Navigation principale ─────────────────────────────────────────────────────
@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AuthScreen.Logo.route
) {
    val context         = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            repository      = AuthRepository(),
            userPreferences = userPreferences
        )
    )

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(AuthScreen.Logo.route){
            LogoScreen (
                onFinished = {
                    navController.navigate(AuthScreen.Splash.route) {
                        popUpTo(AuthScreen.Logo.route) { inclusive = true }
                    }
                }
            )

        }
        /* ═══════════════════ SPLASH ═══════════════════ */
        composable(AuthScreen.Splash.route) {
            SplashScreen(
                onGetStartedClick = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        /* ═══════════════════ LOGIN ═══════════════════ */
        composable(AuthScreen.Login.route) {
            LoginScreenAuth(
                viewModel             = authViewModel,
                onBackClick           = { navController.popBackStack() },
                onSignUpClick         = { navController.navigate(AuthScreen.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(AuthScreen.ForgotPassword.route) },
                onSuccess             = {
                    navController.navigate(AuthScreen.Home.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        /* ═══════════════════ SIGN UP ═══════════════════ */
        composable(AuthScreen.SignUp.route) {
            SignUpScreen(
                viewModel     = authViewModel,
                onBackClick   = { navController.popBackStack() },
                onSuccess     = {},
                onVerifyEmail = { email ->
                    navController.navigate("${AuthScreen.VerifyEmail.route}/$email")
                }
            )
        }

        composable("${AuthScreen.VerifyEmail.route}/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyEmailScreen(
                viewModel   = authViewModel,
                email       = email,
                onSuccess   = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.SignUp.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ═══════════════════ FORGOT PASSWORD ═══════════════════ */
        composable(AuthScreen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick  = { navController.popBackStack() },
                onResetClick = { navController.popBackStack() }
            )
        }

        /* ═══════════════════ HOME ═══════════════════ */
        composable(AuthScreen.Home.route) {
            FoodDeliveryScreen(
                onLogoutClick = {
                    CartManager.clearCart()
                    authViewModel.logout()
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOrdersClick       = { navController.navigate(AuthScreen.Orders.route) },
                onCartClick         = { navController.navigate(AuthScreen.Cart.route) },
                onNotificationClick = { navController.navigate(AuthScreen.Notification.route) },
                onDishClick         = { dishId ->
                    navController.navigate(AuthScreen.DishDetail.createRoute(dishId))
                },
                userPhone = userPreferences.getUserPhone() ?: "",
                userName  = userPreferences.getUserName()  ?: ""
            )
        }

        /* ═══════════════════ NOTIFICATIONS ═══════════════════ */
        composable(AuthScreen.Notification.route) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ═══════════════════ CART ═══════════════════ */
        composable(AuthScreen.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                // ✅ NE vide PAS le panier ici — c'est CheckoutScreen qui le fait après succès API
                onCheckout  = { navController.navigate(AuthScreen.Checkout.route) }
            )
        }

        /* ═══════════════════ CHECKOUT ═══════════════════ */
        composable(AuthScreen.Checkout.route) {
            val userId = userPreferences.getUserId() ?: -1
            CheckoutScreen(
                userId      = userId,
                onBackClick = { navController.popBackStack() },
                // ✅ Succès : efface Cart + Checkout de la pile, va sur Orders
                onOrderSuccess = {
                    navController.navigate(AuthScreen.Orders.route) {
                        popUpTo(AuthScreen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        /* ═══════════════════ ORDERS ═══════════════════ */
        composable(AuthScreen.Orders.route) {
            val userId = userPreferences.getUserId() ?: -1
            OrdersScreen(
                onBackClick = { navController.popBackStack() },
                userId      = userId
            )
        }

        /* ═══════════════════ DISH DETAIL ═══════════════════ */
        composable(
            route     = AuthScreen.DishDetail.route,
            arguments = listOf(navArgument("dishId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dishId = backStackEntry.arguments?.getString("dishId")
            val dish   = FoodData.dishes.find { it.id == dishId }
            dish?.let {
                DishDetailScreen(
                    dish        = it,
                    onBackClick = { navController.popBackStack() },
                    onAddToCart = { quantity ->
                        CartManager.addItem(it)
                        CartManager.updateQuantity(it.id, quantity)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/* ═══════════════════ HOST ═══════════════════ */
@Composable
fun AuthNavigationHost() {
    AuthNavigation()
}