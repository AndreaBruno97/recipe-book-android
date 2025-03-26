package com.example.recipebook.ui.composables.commonComposable.TagFormBody

import com.example.recipebook.data.objects.tag.Tag
import org.mongodb.kbson.ObjectId

data class TagUiState(
    val tagDetails: TagDetails = TagDetails(),
    val isEntryValid: Boolean = false
)

data class TagDetails(
    val _id: ObjectId? = null,
    var name: String = ""
)

fun Tag.toTagDetails(): TagDetails = TagDetails(
    _id = _id,
    name = name
)

fun TagDetails.toTag(): Tag = Tag(
    _id = _id ?: ObjectId(),
    name = name
)

fun Tag.toTagUiState(isEntryValid: Boolean = false): TagUiState = TagUiState(
    tagDetails = this.toTagDetails(),
    isEntryValid = isEntryValid
)

fun validateInput(uiState: TagDetails): Boolean{
    return with(uiState){
        name.isNotBlank()
    }
}