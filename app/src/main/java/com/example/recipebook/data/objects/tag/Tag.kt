package com.example.recipebook.data.objects.tag

import androidx.compose.ui.graphics.Color
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Tag : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var color: Int = 0
    var icon: String? = null

    @Ignore
    val colorObj: Color
        get() = Color(color)

    constructor()

    constructor(
        _id: ObjectId = ObjectId(),
        name: String,
        color: Int = 0,
        icon: String? = null
    ) {
        this._id = _id
        this.name = name
        this.color = color
        this.icon = icon
    }
}