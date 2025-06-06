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

    var validateName by mutableStateOf(false)
        private set

    fun isNamePresent(): Boolean {
        return tagRepository.isNamePresent(tagUiState.tagDao.toTag())
    }

    fun updateUiState(tagDao: TagDao) {
        tagUiState = TagUiState(tagDao = tagDao)
        validateName = false
    }

    suspend fun updateTag(): Boolean {
        val isNameUnique = isNamePresent() == false
        val isInputValid = tagUiState.tagDao.validateInput()
        val saveTag = isNameUnique && isInputValid

        if (saveTag) {
            tagRepository.updateTag(tagUiState.tagDao.toTag())
        } else {
            validateName = true
        }

        return saveTag
    }
}