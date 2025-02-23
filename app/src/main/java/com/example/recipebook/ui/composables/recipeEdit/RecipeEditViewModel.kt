package com.example.recipebook.ui.composables.recipeEdit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.recipe.Recipe
import com.example.recipebook.data.recipe.RecipeRepository
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeDetails
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipe
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipeUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class RecipeEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
): ViewModel() {
    var recipeUiState by mutableStateOf(RecipeUiState())
        private set

    private val recipeIdString: String = checkNotNull(savedStateHandle[RecipeEditDestination.recipeIdArg])
    private val recipeId: ObjectId = ObjectId(recipeIdString)

    private fun validateInput(uiState: RecipeDetails = recipeUiState.recipeDetails): Boolean{
        return with(uiState){
            name.isNotBlank()
        }
    }

    fun updateUiState(recipeDetails: RecipeDetails){
        recipeUiState =
            RecipeUiState(recipeDetails = recipeDetails, isEntryValid = validateInput(recipeDetails))
    }

    suspend fun updateRecipe(){
        if(validateInput(recipeUiState.recipeDetails)){
            recipeRepository.updateRecipe(recipeUiState.recipeDetails.toRecipe())
        }
    }

    init{
        viewModelScope.launch {
            recipeUiState = recipeRepository.getRecipeById(recipeId)
                .filterNotNull()
                .first()
                .toRecipeUiState(true)
        }
    }
}