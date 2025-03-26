package com.example.recipebook.ui.composables.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(recipeRepository: RecipeRepository): ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        recipeRepository.getRecipes().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val recipeList: List<Recipe> = listOf())