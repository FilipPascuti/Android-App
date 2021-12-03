package com.ilazar.myapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ilazar.myapp.auth.LoginScreen
import com.ilazar.myapp.todo.ItemScreen
import com.ilazar.myapp.todo.items.ItemsScreen

@Composable
fun MyAppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "login"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("items") {
            ItemsScreen(navController = navController)
        }
        composable(
            "item?itemId={itemId}",
            arguments = listOf(navArgument("itemId") { nullable = true })
        ) {
            ItemScreen(navController = navController, itemId = it.arguments?.getString("itemId"))
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
    }
}
