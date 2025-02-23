package com.example.recipebook.ui.commonComposable.recipeFormBody

import com.example.recipebook.data.recipe.Recipe
import org.mongodb.kbson.ObjectId

data class RecipeUiState(
    val recipeDetails: RecipeDetails = RecipeDetails(),
    val isEntryValid: Boolean = false
)

data class RecipeDetails(
    val _id: ObjectId? = null,
    val name: String = ""
)

fun RecipeDetails.toRecipe(): Recipe = Recipe(
    _id = _id ?: ObjectId(),
    name =  name
)

fun Recipe.toRecipeUiState(isEntryValid: Boolean = false): RecipeUiState = RecipeUiState(
    recipeDetails = this.toRecipeDetails(),
    isEntryValid = isEntryValid
)

fun Recipe.toRecipeDetails(): RecipeDetails = RecipeDetails(
    _id = _id,
    name = name
)