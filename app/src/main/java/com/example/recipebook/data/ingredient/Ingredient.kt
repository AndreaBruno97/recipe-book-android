package com.example.recipebook.data.ingredient

import io.realm.kotlin.types.EmbeddedRealmObject

class Ingredient: EmbeddedRealmObject {
    var name: String = ""
    var value: String = ""

    constructor()

    constructor(name: String, value: String){
        this.name = name
        this.value = value
    }
}