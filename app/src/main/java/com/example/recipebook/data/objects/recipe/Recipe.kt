package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.tag.Tag
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Recipe : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""

    var methodList: RealmList<String> = realmListOf()
    var ingredients: RealmList<Ingredient> = realmListOf()
    var tags: RealmList<Tag> = realmListOf()

    var servingsNum: Int? = null
    var prepTimeMinutes: Int? = null
    var cookTimeMinutes: Int? = null

    var isFavorite: Boolean = false

    constructor()

    constructor(
        _id: ObjectId = ObjectId(),
        name: String,
        methodList: RealmList<String> = realmListOf(),
        ingredients: RealmList<Ingredient> = realmListOf(),
        tags: RealmList<Tag> = realmListOf(),
        servingsNum: Int? = null,
        prepTimeMinutes: Int? = null,
        cookTimeMinutes: Int? = null,
        isFavorite: Boolean = false,
    ) {
        this._id = _id
        this.name = name
        this.methodList = methodList
        this.ingredients = ingredients
        this.tags = tags
        this.servingsNum = servingsNum
        this.prepTimeMinutes = prepTimeMinutes
        this.cookTimeMinutes = cookTimeMinutes
        this.isFavorite = isFavorite
    }
}

