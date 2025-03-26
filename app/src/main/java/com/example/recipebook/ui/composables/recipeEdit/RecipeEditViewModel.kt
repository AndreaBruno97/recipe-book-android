package com.example.recipebook.ui.composables.recipeEdit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagRepository
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.TagDetails
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeDetails
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeFormBodyTagListUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipe
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipeUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.validateInput
import com.example.recipebook.ui.composables.home.HomeUiState
import com.example.recipebook.ui.composables.home.HomeViewModel
import com.example.recipebook.ui.composables.home.HomeViewModel.Companion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class RecipeEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository,
    tagRepository: TagRepository
): ViewModel() {
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

    private val recipeIdString: String = checkNotNull(savedStateHandle[RecipeEditDestination.recipeIdArg])
    private val recipeId: ObjectId = ObjectId(recipeIdString)

    fun updateUiState(recipeDetails: RecipeDetails){
        recipeUiState =
            RecipeUiState(recipeDetails = recipeDetails, isEntryValid = validateInput(recipeDetails))
    }

    suspend fun updateRecipe(){
        if(validateInput(recipeUiState.recipeDetails)){
            recipeRepository.updateRecipe(recipeUiState.recipeDetails.toRecipe())
        }
    }

    fun openTagListPopup(){
        isTagListPopupOpen = true
    }

    fun closeTagListPopup(){
        isTagListPopupOpen = false
    }

    init{
        viewModelScope.launch {
            recipeUiState = recipeRepository.getRecipeById(recipeId)
                .filterNotNull()
                .first()
                .toRecipeUiState(true)
        }
    }

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }
}