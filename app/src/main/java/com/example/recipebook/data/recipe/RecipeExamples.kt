package com.example.recipebook.data.recipe

import com.example.recipebook.data.ingredient.IngredientExamples
import io.realm.kotlin.ext.realmListOf
import org.mongodb.kbson.BsonObjectId

class RecipeExamples {

    companion object{
        val recipe1: Recipe = Recipe(
            _id = BsonObjectId("111111111111111111111111"),
            name = "Esempio ricetta 1",
            method = "Esempio procedimento 1",
            ingredients = realmListOf(
                IngredientExamples.ingredientA,
                IngredientExamples.ingredientB
            )
        )

        val recipe2: Recipe = Recipe(
            _id = BsonObjectId("222222222222222222222222"),
            name = "Esempio ricetta 2",
            method = "Esempio procedimento 2",
            ingredients = realmListOf(
                IngredientExamples.ingredientC,
                IngredientExamples.ingredientD
            )
        )

        val recipe3: Recipe = Recipe(
            _id = BsonObjectId("333333333333333333333333"),
            name = "Esempio ricetta 3",
            method = "Esempio procedimento 3",
            ingredients = realmListOf(
                IngredientExamples.ingredientA,
                IngredientExamples.ingredientB,
                IngredientExamples.ingredientC,
                IngredientExamples.ingredientD
            )
        )

        val recipeList: List<Recipe> = listOf(recipe1, recipe2, recipe3)
    }
}