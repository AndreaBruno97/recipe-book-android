@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.recipeCreate

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.utility.ImageManagerViewModel
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.getRecipeImagePath
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.mongodb.kbson.ObjectId

class RecipeCreateViewModel(
    private val recipeRepository: RecipeRepository
) : ImageManagerViewModel() {

    var recipeUiState by mutableStateOf(RecipeUiState())
        private set

    fun updateUiState(recipeDao: RecipeDao) {
        recipeUiState = RecipeUiState(recipeDao = recipeDao)
    }

    suspend fun saveRecipe(context: Context): ObjectId? {
        if (recipeUiState.recipeDao.validateInput()) {
            val recipeId = recipeRepository.addRecipe(recipeUiState.recipeDao.toRecipe())

            val recipeFolderPath = getRecipeFolderPath(recipeId)
            val recipeFilePath = getRecipeImagePath(recipeId)

            saveImage(recipeFolderPath, recipeFilePath, context)

            return recipeId
        }

        updateUiState(recipeUiState.recipeDao.getInputValidationCopy())

        return null
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}