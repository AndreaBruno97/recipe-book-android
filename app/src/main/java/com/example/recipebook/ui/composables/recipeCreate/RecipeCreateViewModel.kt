@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.recipeCreate

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.TagRepository
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeFormBodyTagListUiState
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeForm_TagListFilterState
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.utility.ImageManagerViewModel
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.getRecipeImagePath
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mongodb.kbson.ObjectId

class RecipeCreateViewModel(
    private val recipeRepository: RecipeRepository,
    tagRepository: TagRepository
) : ImageManagerViewModel() {

    //region Recipe

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

    //endregion

    //region Tag List

    private val _tagListFilterState = MutableStateFlow(RecipeForm_TagListFilterState())
    val tagListFilterState = _tagListFilterState.asStateFlow()

    var tagListUiState: StateFlow<RecipeFormBodyTagListUiState> =
        _tagListFilterState
            .flatMapLatest { filter ->
                tagRepository.getTagFiltered(filter.filterNameOrNull)
            }
            .map { RecipeFormBodyTagListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RecipeFormBodyTagListUiState()
            )

    var isTagListPopupOpen by mutableStateOf(false)
        private set

    fun openTagListPopup() {
        isTagListPopupOpen = true
    }

    fun closeTagListPopup() {
        isTagListPopupOpen = false
    }

    fun updateFilterName(newFilterName: String) {
        _tagListFilterState.value = _tagListFilterState.value.copy(
            filterName = newFilterName
        )
    }

    //endregion

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}