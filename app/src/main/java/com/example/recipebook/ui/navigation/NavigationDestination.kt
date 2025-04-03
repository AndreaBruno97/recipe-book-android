package com.example.recipebook.ui.navigation

import org.mongodb.kbson.ObjectId

/**
 * Interface to describe the navigation destinations for the app
 */
interface NavigationDestination {
    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    val titleRes: Int
}

interface NavigationDestinationNoParams : NavigationDestination {
    fun getNavigateString(): String {
        return route
    }
}

interface NavigationDestinationRecipeId : NavigationDestination {
    fun getNavigateString(recipeId: ObjectId): String {
        return "${route}/${recipeId.toHexString()}"
    }
}