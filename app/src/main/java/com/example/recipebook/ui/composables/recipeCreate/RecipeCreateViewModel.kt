@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.recipeCreate

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.network.Network
import com.example.recipebook.network.SupportedWebsites
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.utility.ImageManagerViewModel
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.getRecipeImagePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.net.MalformedURLException
import java.net.URL

enum class RecipeWebsiteUrlErrors {
    INVALID_URL, WEBSITE_NOT_MANAGED, LOADING_ERROR
}

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

    var recipeWebsiteUrl by mutableStateOf("")
        private set
    var validateRecipeWebsiteUrl: RecipeWebsiteUrlErrors? by mutableStateOf(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isRecipeFromWebsiteSectionVisible by mutableStateOf(true)
        private set

    fun showRecipeFromWebsiteSection() {
        isRecipeFromWebsiteSectionVisible = true
    }

    fun hideRecipeFromWebsiteSection() {
        isRecipeFromWebsiteSectionVisible = false
    }

    fun updateRecipeWebsiteUrl(url: String) {
        recipeWebsiteUrl = url
        validateRecipeWebsiteUrl = null
    }

    fun loadRecipeFromWebsite(compositionContext: Context) {

        try {
            val parsedRecipeWebsiteUrl = URL(recipeWebsiteUrl)

            if (parsedRecipeWebsiteUrl.host !in SupportedWebsites.entries.map { it.host }) {
                validateRecipeWebsiteUrl = RecipeWebsiteUrlErrors.WEBSITE_NOT_MANAGED
                return
            }
        } catch (e: MalformedURLException) {
            validateRecipeWebsiteUrl = RecipeWebsiteUrlErrors.INVALID_URL
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true

            val recipeResult = Network.getPage(recipeWebsiteUrl, compositionContext.cacheDir)

            if (recipeResult.success) {
                if (recipeResult.recipe != null) {
                    updateUiState(recipeResult.recipe.toRecipeDao())
                }

                if (recipeResult.imageBitmap != null && recipeResult.imagePath != null) {
                    tempImage = recipeResult.imageBitmap
                    tempFileUrlDirectString = recipeResult.imagePath
                    isFileChanged = true
                    tempFileUrl = null
                }

                validateRecipeWebsiteUrl = null
            } else {
                validateRecipeWebsiteUrl = RecipeWebsiteUrlErrors.LOADING_ERROR
            }

            isLoading = false
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}