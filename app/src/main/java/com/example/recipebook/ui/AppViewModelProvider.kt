package com.example.recipebook.ui

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recipebook.RecipeBookApplication
import com.example.recipebook.ui.home.HomeViewModel
import com.example.recipebook.ui.recipeCreate.RecipeCreateViewModel
import com.example.recipebook.ui.recipeDetails.RecipeDetailsViewModel
import com.example.recipebook.ui.recipeEdit.RecipeEditViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(
                recipeRepository = RecipeBookApplication.recipeRepository
            )
        }

        initializer {
            RecipeCreateViewModel(
                recipeRepository = RecipeBookApplication.recipeRepository
            )
        }

        initializer {
            RecipeEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                recipeRepository = RecipeBookApplication.recipeRepository

            )
        }

        initializer {
            RecipeDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                recipeRepository = RecipeBookApplication.recipeRepository
            )
        }
    }
}