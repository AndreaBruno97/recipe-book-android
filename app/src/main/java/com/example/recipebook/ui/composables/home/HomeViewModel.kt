@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.home

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mongodb.kbson.ObjectId

class HomeViewModel(recipeRepository: RecipeRepository) : ViewModel() {
    private val _filterState = MutableStateFlow(RecipeListFilterState())
    val filterState = _filterState.asStateFlow()

    val homeUiState: StateFlow<HomeUiState> =
        _filterState
            .flatMapLatest { filter ->
                recipeRepository.getRecipesFiltered(
                    filter.filterNameOrNull,
                    filter.filterIsFavorite
                )
            }.map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    private val recipeImageMap: MutableMap<String, ImageBitmap?> = mutableStateMapOf()

    fun loadRecipeImage(recipeId: ObjectId, context: Context): ImageBitmap? {
        var recipeImage: ImageBitmap? = null

        val recipeIdString = recipeId.toHexString()
        if (!recipeImageMap.containsKey(recipeIdString)) {
            recipeImage = com.example.recipebook.ui.composables.common.utility.loadRecipeImage(
                recipeId,
                context
            )

            recipeImageMap[recipeIdString] = recipeImage
        } else {
            recipeImage = recipeImageMap[recipeIdString]
        }

        return recipeImage
    }

    fun updateFilter(newFilterState: RecipeListFilterState) {
        _filterState.value = newFilterState
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RecipeListFilterState(
    val filterName: String = "",
    val filterIsFavorite: Boolean = false
) {
    val filterNameOrNull = filterName.ifBlank { null }
}

data class HomeUiState(val recipeList: List<Recipe> = listOf())