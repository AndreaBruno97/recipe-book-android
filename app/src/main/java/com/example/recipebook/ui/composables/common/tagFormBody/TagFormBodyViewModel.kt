package com.example.recipebook.ui.composables.common.tagFormBody

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class TagFormBodyViewModel(
    private val tagRepository: TagRepository
) : ViewModel() {
    var tagUiState by mutableStateOf(TagUiState())
        private set

    fun loadTag(tagId: ObjectId?) {
        if (tagId != null) {
            viewModelScope.launch {
                tagUiState = tagRepository.getTagById(tagId)
                    .filterNotNull()
                    .first()
                    .toTagUiState()
            }
        } else {
            tagUiState = TagUiState()
        }
    }

    fun updateUiState(tagDao: TagDao) {
        tagUiState = TagUiState(tagDao = tagDao)
    }

    suspend fun updateTag() {
        if (tagUiState.tagDao.validateInput()) {
            tagRepository.updateTag(tagUiState.tagDao.toTag())
        }
    }
}