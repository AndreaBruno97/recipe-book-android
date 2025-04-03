package com.example.recipebook.ui.composables.recipeCreate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.TagRepository
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeFormBodyTagListUiState
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
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

    fun updateUiState(recipeDao: RecipeDao) {
        recipeUiState = RecipeUiState(recipeDao = recipeDao)
    }

    suspend fun saveRecipe(): ObjectId? {
        if (recipeUiState.recipeDao.validateInput()) {
            return recipeRepository.addRecipe(recipeUiState.recipeDao.toRecipe())
        }
        return null
    }

    fun openTagListPopup() {
        isTagListPopupOpen = true
    }

    fun closeTagListPopup() {
        isTagListPopupOpen = false
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}