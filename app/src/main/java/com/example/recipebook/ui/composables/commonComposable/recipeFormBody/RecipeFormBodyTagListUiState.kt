package com.example.recipebook.ui.composables.commonComposable.recipeFormBody

import com.example.recipebook.data.objects.tag.Tag

data class RecipeFormBodyTagListUiState(
    val tagDetailList: List<Tag> = listOf()
)