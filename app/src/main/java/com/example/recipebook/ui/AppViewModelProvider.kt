package com.example.recipebook.ui

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recipebook.RecipeBookApplication
import com.example.recipebook.ui.composables.common.tagFormBody.TagFormBodyViewModel
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorViewModel
import com.example.recipebook.ui.composables.common.utility.ImageManagerViewModel
import com.example.recipebook.ui.composables.home.HomeViewModel
import com.example.recipebook.ui.composables.recipeCreate.RecipeCreateViewModel
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsViewModel
import com.example.recipebook.ui.composables.recipeEdit.RecipeEditViewModel
import com.example.recipebook.ui.composables.tagList.TagListViewModel

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

        initializer {
            TagListViewModel(
                tagRepository = RecipeBookApplication.tagRepository
            )
        }

        initializer {
            TagFormBodyViewModel(
                tagRepository = RecipeBookApplication.tagRepository
            )
        }

        initializer {
            TagListSelectorViewModel(
                tagRepository = RecipeBookApplication.tagRepository
            )
        }

        initializer {
            ImageManagerViewModel()
        }

    }
}