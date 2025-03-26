package com.example.recipebook.ui.composables.commonComposable.TagFormBody

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.tag.TagRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class TagFormBodyViewModel(
    private val tagRepository: TagRepository
): ViewModel() {
    var tagUiState by mutableStateOf(TagUiState())
        private set

    fun loadTag(tagId: ObjectId?){
        if(tagId != null){
            viewModelScope.launch {
                tagUiState = tagRepository.getTagById(tagId)
                    .filterNotNull()
                    .first()
                    .toTagUiState(true)
            }
        }
        else{
            tagUiState = TagUiState()
        }
    }

    fun updateUiState(tagDetails: TagDetails){
        tagUiState =
            TagUiState(tagDetails = tagDetails, isEntryValid = validateInput(tagDetails))
    }

    suspend fun updateTag(){
        if(validateInput(tagUiState.tagDetails)){
            tagRepository.updateTag(tagUiState.tagDetails.toTag())
        }
    }
}