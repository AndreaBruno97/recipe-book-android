package com.example.recipebook.ui.composables.common.recipeFormBody

import com.example.recipebook.data.objects.tag.Tag

data class RecipeFormBodyTagListUiState(
    val tagDetailList: List<Tag> = listOf()
)