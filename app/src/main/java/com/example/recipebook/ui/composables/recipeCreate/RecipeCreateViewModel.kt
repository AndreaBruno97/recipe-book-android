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
import org.mongodb.kbson.ObjectId

class RecipeCreateViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

    var recipeUiState by mutableStateOf(RecipeUiState())
        private  set

    fun updateUiState(recipeDetails: RecipeDetails){
        recipeUiState =
            RecipeUiState(recipeDetails = recipeDetails, isEntryValid = validateInput(recipeDetails))
    }

    private fun validateInput(uiState: RecipeDetails = recipeUiState.recipeDetails): Boolean{
        return with(uiState){
            name.isNotBlank()
        }
    }

    suspend fun saveRecipe(): ObjectId? {
        if(validateInput()){
            return recipeRepository.addRecipe(recipeUiState.recipeDetails.toRecipe())
        }
        return null
    }
}