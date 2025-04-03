package com.example.recipebook.data.objects.tag

import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

data class TagDao(
    val _id: ObjectId? = null,
    var name: String = ""
) {
    fun toTag(): Tag = Tag(
        _id = _id ?: BsonObjectId(),
        name = name
    )

    fun validateInput(): Boolean {
        return name.isNotBlank()
    }
}

fun Tag.toTagDao(): TagDao = TagDao(
    _id = _id,
    name = name
)