package com.example.recipebook.data.objects.ingredientGroup

import com.example.recipebook.data.objects.ingredient.IngredientExamples
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList

class IngredientGroupExamples {
    companion object {
        val ingredientGroupA: IngredientGroup = IngredientGroup(
            title = "Gruppo A",
            ingredientList = IngredientExamples.ingredientList.toRealmList()
        )

        val ingredientGroupB: IngredientGroup = IngredientGroup(
            title = "Gruppo B",
            ingredientList = realmListOf(
                IngredientExamples.ingredientA,
                IngredientExamples.ingredientB
            )
        )

        val ingredientGroupNoName: IngredientGroup = IngredientGroup(
            title = null,
            ingredientList = realmListOf(
                IngredientExamples.ingredientC,
                IngredientExamples.ingredientD
            )
        )

        val ingredientGroupList: List<IngredientGroup> =
            listOf(ingredientGroupA, ingredientGroupB, ingredientGroupNoName)
    }
}