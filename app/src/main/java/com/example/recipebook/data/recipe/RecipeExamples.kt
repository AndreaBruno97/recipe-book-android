package com.example.recipebook.data.recipe

import org.mongodb.kbson.BsonObjectId

class RecipeExamples {

    companion object{
        val recipe1: Recipe = Recipe(
            _id = BsonObjectId("111111111111111111111111"),
            name = "Esempio ricetta 1"
        )

        val recipe2: Recipe = Recipe(
            _id = BsonObjectId("222222222222222222222222"),
            name = "Esempio ricetta 2"
        )

        val recipe3: Recipe = Recipe(
            _id = BsonObjectId("333333333333333333333333"),
            name = "Esempio ricetta 3"
        )

        val recipeList: List<Recipe> = listOf(recipe1, recipe2, recipe3)
    }
}