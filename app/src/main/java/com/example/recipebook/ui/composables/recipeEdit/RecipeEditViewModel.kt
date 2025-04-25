package com.example.recipebook.ui.composables.recipeEdit

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.TagRepository
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeFormBodyTagListUiState
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.recipeFormBody.toRecipeUiState
import com.example.recipebook.ui.composables.common.utility.ImageManagerViewModel
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.getRecipeImagePath
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
) : ImageManagerViewModel() {
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

    private val recipeIdString: String =
        checkNotNull(savedStateHandle[RecipeEditDestination.recipeIdArg])
    var recipeId: ObjectId = ObjectId(recipeIdString)
        private set

    fun updateUiState(recipeDao: RecipeDao) {
        recipeUiState = RecipeUiState(recipeDao = recipeDao)
    }

    suspend fun updateRecipe(context: Context) {
        if (recipeUiState.recipeDao.validateInput()) {
            recipeRepository.updateRecipe(recipeUiState.recipeDao.toRecipe())

            val recipeFolderPath = getRecipeFolderPath(recipeId)
            val recipeFilePath = getRecipeImagePath(recipeId)

            saveImage(recipeFolderPath, recipeFilePath, context)
        }
    }

    fun openTagListPopup() {
        isTagListPopupOpen = true
    }

    fun closeTagListPopup() {
        isTagListPopupOpen = false
    }

    fun loadRecipeImage(context: Context) {
        if (isFileChanged == false && tempImage == null) {
            tempImage = com.example.recipebook.ui.composables.common.utility.loadRecipeImage(
                recipeId,
                context
            )
        }
    }

    init {
        viewModelScope.launch {
            recipeUiState = recipeRepository.getRecipeById(recipeId)
                .filterNotNull()
                .first()
                .toRecipeUiState()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}