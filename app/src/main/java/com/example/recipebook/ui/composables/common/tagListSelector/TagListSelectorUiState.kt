package com.example.recipebook.ui.composables.common.tagListSelector

import com.example.recipebook.data.objects.tag.Tag

data class TagListSelectorUiState(
    val tagDetailList: List<Tag> = listOf()
)

data class TagListSelectorFilterUiState(val filterName: String = "") {
    val filterNameOrNull = filterName.ifBlank { null }
}