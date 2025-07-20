@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.recipeEdit

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.recipeFormBody.toRecipeUiState
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.saveRecipeImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class RecipeEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    var recipeUiState by mutableStateOf(RecipeUiState())
        private set

    private val recipeIdString: String =
        checkNotNull(savedStateHandle[RecipeEditDestination.recipeIdArg])
    var recipeId: ObjectId = ObjectId(recipeIdString)
        private set

    var isRecipeImageChanged by mutableStateOf(false)
        private set

    fun updateUiState(recipeDao: RecipeDao) {
        recipeUiState = recipeUiState.copy(recipeDao = recipeDao)
    }

    fun updateUiStateImage(recipeImage: ImageBitmap?, recipeImageTmpPath: String?) {
        recipeUiState =
            recipeUiState.copy(recipeImage = recipeImage, recipeImageTmpPath = recipeImageTmpPath)
        isRecipeImageChanged = true
    }

    suspend fun updateRecipe(context: Context): Boolean {
        if (recipeUiState.recipeDao.validateInput()) {
            recipeRepository.updateRecipe(recipeUiState.recipeDao.toRecipe())

            val recipeFolderPath = getRecipeFolderPath(recipeId)

            if (isRecipeImageChanged) {
                saveRecipeImage(
                    recipeFolderPath,
                    recipeUiState.recipeImageTmpPath,
                    context
                )
            }

            return true
        }

        updateUiState(recipeUiState.recipeDao.getInputValidationCopy())

        return false
    }

    fun loadRecipeImage(context: Context) {
        if (isRecipeImageChanged == false && recipeUiState.recipeImage == null) {
            val tempImage = com.example.recipebook.ui.composables.common.utility.loadRecipeImage(
                recipeId,
                context
            )

            recipeUiState = recipeUiState.copy(recipeImage = tempImage)
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