package com.example.recipebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recipebook.ui.composables.home.HomeDestination
import com.example.recipebook.ui.composables.home.HomeScreen
import com.example.recipebook.ui.composables.recipeCreate.RecipeCreateDestination
import com.example.recipebook.ui.composables.recipeCreate.RecipeCreateScreen
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsDestination
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsScreen
import com.example.recipebook.ui.composables.recipeEdit.RecipeEditDestination
import com.example.recipebook.ui.composables.recipeEdit.RecipeEditScreen

@Composable
fun RecipeBookNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ){
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToRecipeCreate = { navController.navigate(RecipeCreateDestination.getNavigateString()) },
                navigateToRecipeDetails = { navController.navigate(RecipeDetailsDestination.getNavigateString(it))}
            )
        }

        composable(route = RecipeCreateDestination.route) {
            RecipeCreateScreen(
                navigateToRecipeDetails = { navController.navigate(RecipeDetailsDestination.getNavigateString(it))},
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = RecipeDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RecipeDetailsDestination.recipeIdArg){
                type = NavType.StringType
            })
        ) {
            RecipeDetailsScreen(
                navigateToEditRecipe = { navController.navigate(RecipeEditDestination.getNavigateString(it)) },
                navigateBack = { navController.navigate(HomeDestination.getNavigateString()) }
            )
        }

        composable(
            route = RecipeEditDestination.routeWithArgs,
            arguments = listOf(navArgument(RecipeEditDestination.recipeIdArg){
                type = NavType.StringType
            })
        ) {
            RecipeEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}