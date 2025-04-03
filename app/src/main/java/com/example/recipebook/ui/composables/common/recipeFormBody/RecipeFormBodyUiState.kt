package com.example.recipebook.ui.composables.common.recipeFormBody

import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.toRecipeDao

data class RecipeUiState(
    val recipeDao: RecipeDao = RecipeDao()
)

fun Recipe.toRecipeUiState(): RecipeUiState = RecipeUiState(
    recipeDao = this.toRecipeDao()
)