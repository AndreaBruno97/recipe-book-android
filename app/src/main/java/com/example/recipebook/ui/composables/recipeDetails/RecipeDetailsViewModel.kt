package com.example.recipebook.ui.composables.recipeDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.recipebook.data.objects.recipe.Recipe
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mongodb.kbson.ObjectId

class RecipeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    private val recipeIdString: String = checkNotNull(savedStateHandle[RecipeDetailsDestination.recipeIdArg])
    private val recipeId: ObjectId = ObjectId(recipeIdString)

    val uiState: StateFlow<RecipeDetailsUiState> =
        recipeRepository.getRecipeById(recipeId)
            .filterNotNull()
            .map{
                RecipeDetailsUiState(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RecipeDetailsUiState()
            )

    suspend fun deleteRecipe(){
        recipeRepository.removeRecipe(uiState.value.recipe)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RecipeDetailsUiState(
    val recipe: Recipe = Recipe()
)