package com.example.recipebook.data.objects.ingredientGroup

import com.example.recipebook.data.objects.ingredient.Ingredient
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList

class IngredientGroup : EmbeddedRealmObject {
    var title: String? = null
    var ingredientList: RealmList<Ingredient> = realmListOf()

    constructor()

    constructor(
        title: String?,
        ingredientList: RealmList<Ingredient> = realmListOf()
    ) {
        this.title = title
        this.ingredientList = ingredientList
    }
}