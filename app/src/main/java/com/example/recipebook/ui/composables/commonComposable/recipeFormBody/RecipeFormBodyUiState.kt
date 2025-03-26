package com.example.recipebook.ui.composables.commonComposable.recipeFormBody

import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.TagDetails
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.toTag
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.toTagDetails
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId

data class RecipeUiState(
    val recipeDetails: RecipeDetails = RecipeDetails(),
    val isEntryValid: Boolean = false
)

//region IngredientDetails
data class IngredientDetails(
    var name: String = "",
    var value: String = ""
)

fun Ingredient.toIngredientDetails(): IngredientDetails = IngredientDetails(
    name = name,
    value = value
)

fun IngredientDetails.toIngredient(): Ingredient = Ingredient(
    name = name,
    value = value
)
//endregion

//region RecipeDetails
data class RecipeDetails(
    val _id: ObjectId? = null,
    val name: String = "",
    var method: String = "",
    var ingredients: List<IngredientDetails> = listOf(),
    var tags: List<TagDetails> = listOf()
)

fun RecipeDetails.toRecipe(): Recipe = Recipe(
    _id = _id ?: ObjectId(),
    name =  name,
    method =  method,
    ingredients = ingredients.map { it.toIngredient() }.toRealmList(),
    tags = tags.map { it.toTag() }.toRealmList()
)

fun Recipe.toRecipeUiState(isEntryValid: Boolean = false): RecipeUiState = RecipeUiState(
    recipeDetails = this.toRecipeDetails(),
    isEntryValid = isEntryValid
)

fun Recipe.toRecipeDetails(): RecipeDetails = RecipeDetails(
    _id = _id,
    name = name,
    method =  method,
    ingredients = ingredients.map { it.toIngredientDetails() },
    tags = tags.map { it.toTagDetails() }
)
//endregion

fun validateInput(uiState: RecipeDetails): Boolean{
    return with(uiState){
        name.isNotBlank() &&
        method.isNotBlank() &&
        ingredients.isNotEmpty() &&
        ingredients.all { it.name.isNotBlank() }
    }
}