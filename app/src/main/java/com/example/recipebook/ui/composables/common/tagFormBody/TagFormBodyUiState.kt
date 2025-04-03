package com.example.recipebook.ui.composables.common.tagFormBody

import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.toTagDao

data class TagUiState(
    val tagDao: TagDao = TagDao()
)

fun Tag.toTagUiState(): TagUiState = TagUiState(
    tagDao = this.toTagDao()
)