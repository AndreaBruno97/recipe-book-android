package com.example.recipebook.data.objects.tag

import org.mongodb.kbson.BsonObjectId

class TagExamples {
    companion object {
        val tag1: Tag = Tag(
            _id = BsonObjectId("111111111111111111111111"),
            name = "Tag 1"
        )

        val tag2: Tag = Tag(
            _id = BsonObjectId("222222222222222222222222"),
            name = "Tag 2"
        )


        val tag3: Tag = Tag(
            _id = BsonObjectId("333333333333333333333333"),
            name = "Tag 3"
        )


        val tag4: Tag = Tag(
            _id = BsonObjectId("444444444444444444444444"),
            name = "Tag 4"
        )

        val tagList: List<Tag> = listOf(tag1, tag2, tag3, tag4)
    }
}