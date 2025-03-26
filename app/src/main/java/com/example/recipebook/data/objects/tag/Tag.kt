package com.example.recipebook.data.objects.tag

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Tag: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var name: String = ""

    constructor()

    constructor(
        _id: ObjectId = ObjectId(),
        name: String
    ){
        this._id = _id
        this.name = name
    }
}