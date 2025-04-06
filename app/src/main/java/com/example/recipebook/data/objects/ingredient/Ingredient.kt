package com.example.recipebook.data.objects.ingredient

import io.realm.kotlin.types.EmbeddedRealmObject

class Ingredient : EmbeddedRealmObject {
    var name: String = ""
    var quantity: Float? = null
    var value: String = ""

    constructor()

    constructor(name: String, quantity: Float?, value: String) {
        this.name = name
        this.quantity = quantity
        this.value = value
    }
}