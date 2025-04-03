package com.example.recipebook.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import com.example.recipebook.ui.composables.tagList.TagListDestination
import com.example.recipebook.ui.composables.tagList.TagListScreen

enum class ScreenSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun RecipeBookNavHost(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screenSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> ScreenSize.SMALL
        WindowWidthSizeClass.Medium -> ScreenSize.MEDIUM
        WindowWidthSizeClass.Expanded -> ScreenSize.LARGE
        else -> ScreenSize.SMALL
    }

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                screenSize = screenSize,
                navigateToRecipeCreate = { navController.navigate(RecipeCreateDestination.getNavigateString()) },
                navigateToRecipeDetails = {
                    navController.navigate(
                        RecipeDetailsDestination.getNavigateString(
                            it
                        )
                    )
                },
                navigateToTagList = { navController.navigate(TagListDestination.getNavigateString()) }
            )
        }

        composable(route = RecipeCreateDestination.route) {
            RecipeCreateScreen(
                screenSize = screenSize,
                navigateToRecipeDetails = {
                    navController.navigate(
                        RecipeDetailsDestination.getNavigateString(
                            it
                        )
                    )
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = RecipeDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RecipeDetailsDestination.recipeIdArg) {
                type = NavType.StringType
            })
        ) {
            RecipeDetailsScreen(
                screenSize = screenSize,
                navigateToEditRecipe = {
                    navController.navigate(
                        RecipeEditDestination.getNavigateString(
                            it
                        )
                    )
                },
                navigateBack = { navController.navigate(HomeDestination.getNavigateString()) }
            )
        }

        composable(
            route = RecipeEditDestination.routeWithArgs,
            arguments = listOf(navArgument(RecipeEditDestination.recipeIdArg) {
                type = NavType.StringType
            })
        ) {
            RecipeEditScreen(
                screenSize = screenSize,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = TagListDestination.route) {
            TagListScreen(
                screenSize = screenSize,
                navigateBack = { navController.navigate(HomeDestination.getNavigateString()) }
            )
        }

    }
}