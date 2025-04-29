package com.example.recipebook.data.objects.tag

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.mongodb.kbson.BsonObjectId

class TagExamples {
    companion object {
        val tag1: Tag = Tag(
            _id = BsonObjectId("111111111111111111111111"),
            name = "Tag 1",
            color = Color.Red.toArgb(),
            icon = "1\uFE0F⃣"

        )

        val tag2: Tag = Tag(
            _id = BsonObjectId("222222222222222222222222"),
            name = "Tag 2",
            color = Color.Black.toArgb(),
            icon = null
        )


        val tag3: Tag = Tag(
            _id = BsonObjectId("333333333333333333333333"),
            name = "Tag 3",
            color = Color.Blue.toArgb(),
            icon = "3\uFE0F⃣"
        )


        val tag4: Tag = Tag(
            _id = BsonObjectId("444444444444444444444444"),
            name = "Tag 4",
            color = Color.Green.toArgb(),
            icon = "4\uFE0F⃣"
        )

        val tagList: List<Tag> = listOf(tag1, tag2, tag3, tag4)
    }
}