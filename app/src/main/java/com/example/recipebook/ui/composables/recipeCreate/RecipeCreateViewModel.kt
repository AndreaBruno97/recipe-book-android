package com.example.recipebook.ui.composables.recipeCreate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.recipebook.data.recipe.Recipe
import com.example.recipebook.data.recipe.RecipeRepository
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeDetails
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipe
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.validateInput
import org.mongodb.kbson.ObjectId

class RecipeCreateViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

    var recipeUiState by mutableStateOf(RecipeUiState())
        private  set

    fun updateUiState(recipeDetails: RecipeDetails){
        recipeUiState =
            RecipeUiState(recipeDetails = recipeDetails, isEntryValid = validateInput(recipeDetails))
    }

    suspend fun saveRecipe(): ObjectId? {
        if(validateInput(recipeUiState.recipeDetails)){
            return recipeRepository.addRecipe(recipeUiState.recipeDetails.toRecipe())
        }
        return null
    }
}