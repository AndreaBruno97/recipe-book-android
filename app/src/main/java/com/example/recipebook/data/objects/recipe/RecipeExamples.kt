package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.objects.ingredient.IngredientExamples
import com.example.recipebook.data.objects.tag.TagExamples
import io.realm.kotlin.ext.realmListOf
import org.mongodb.kbson.BsonObjectId

class RecipeExamples {

    companion object {
        val recipe1: Recipe = Recipe(
            _id = BsonObjectId("111111111111111111111111"),
            name = "Esempio ricetta 1",
            methodList = realmListOf(
                "Esempio procedimento 1.1",
                "Esempio procedimento 1.2"
            ),
            ingredients = realmListOf(
                IngredientExamples.ingredientA,
                IngredientExamples.ingredientB
            ),
            tags = realmListOf(
                TagExamples.tag1,
                TagExamples.tag2
            ),
            servingsNum = 1,
            prepTimeMinutes = 90,
            cookTimeMinutes = 10,
            isFavorite = true
        )

        val recipe2: Recipe = Recipe(
            _id = BsonObjectId("222222222222222222222222"),
            name = "Esempio ricetta 2",
            methodList = realmListOf(
                "Esempio procedimento 2.1",
                "Esempio procedimento 2.2"
            ),
            ingredients = realmListOf(
                IngredientExamples.ingredientC,
                IngredientExamples.ingredientD
            ),
            tags = realmListOf(
                TagExamples.tag3,
                TagExamples.tag4
            ),
            servingsNum = 2,
            prepTimeMinutes = 10,
            cookTimeMinutes = 90,
            isFavorite = false
        )

        val recipe3: Recipe = Recipe(
            _id = BsonObjectId("333333333333333333333333"),
            name = "Esempio ricetta 3",
            methodList = realmListOf(
                "Esempio procedimento 3.1",
                "Esempio procedimento 3.2"
            ),
            ingredients = realmListOf(
                IngredientExamples.ingredientA,
                IngredientExamples.ingredientB,
                IngredientExamples.ingredientC,
                IngredientExamples.ingredientD
            ),
            tags = realmListOf(
                TagExamples.tag1,
                TagExamples.tag2,
                TagExamples.tag3,
                TagExamples.tag4
            ),
            servingsNum = 3,
            prepTimeMinutes = 90,
            cookTimeMinutes = 90,
            isFavorite = false
        )

        val recipeList: List<Recipe> = listOf(recipe1, recipe2, recipe3)
    }
}