package com.example.recipebook.ui.composables.recipeCreate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagRepository
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeDetails
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeFormBodyTagListUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipe
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.validateInput
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mongodb.kbson.ObjectId

class RecipeCreateViewModel(
    private val recipeRepository: RecipeRepository,
    tagRepository: TagRepository
) : ViewModel() {

    var recipeUiState by mutableStateOf(RecipeUiState())
        private set
    var tagListUiState: StateFlow<RecipeFormBodyTagListUiState> =
        tagRepository.getTag().map { RecipeFormBodyTagListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RecipeFormBodyTagListUiState()
            )

    var isTagListPopupOpen by mutableStateOf(false)
        private set

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

    fun openTagListPopup(){
        isTagListPopupOpen = true
    }

    fun closeTagListPopup(){
        isTagListPopupOpen = false
    }
    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }
}