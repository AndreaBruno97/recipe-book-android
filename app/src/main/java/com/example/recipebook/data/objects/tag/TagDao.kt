package com.example.recipebook.data.objects.tag

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

data class TagDao(
    val _id: ObjectId? = null,
    var name: String = "",
    var color: Color = defaultTagColor,
    var icon: String? = null
) {
    fun toTag(): Tag = Tag(
        _id = _id ?: BsonObjectId(),
        name = name,
        color = color.toArgb(),
        icon = icon
    )

    fun validateInput(): Boolean {
        return name.isNotBlank()
    }
}

fun Tag.toTagDao(): TagDao = TagDao(
    _id = _id,
    name = name,
    color = Color(color),
    icon = icon
)