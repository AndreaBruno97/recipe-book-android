package com.example.recipebook.data.recipe

import com.example.recipebook.data.ingredient.Ingredient
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Recipe: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var name: String = ""
    var method: String = ""
    var ingredients: RealmList<Ingredient> = realmListOf()

    constructor()

    constructor(
        _id: ObjectId,
        name: String,
        method: String = "",
        ingredients: RealmList<Ingredient> = realmListOf()
    ) {
        this._id = _id
        this.name = name
        this.method = method
        this.ingredients = ingredients
    }
}

