package com.example.recipebook.data.objects.recipe

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroupExamples
import com.example.recipebook.data.objects.tag.TagExamples
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
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
            ingredientGroupList = IngredientGroupExamples.ingredientGroupList.toRealmList(),
            tagList = realmListOf(
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
            ingredientGroupList = realmListOf(
                IngredientGroupExamples.ingredientGroupA
            ),
            tagList = realmListOf(
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
            ingredientGroupList = realmListOf(
                IngredientGroupExamples.ingredientGroupA,
                IngredientGroupExamples.ingredientGroupB
            ),
            tagList = realmListOf(
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

        val recipeImageBitmap = BitmapFactory
            .decodeResource(
                Resources.getSystem(),
                R.drawable.test_recipe_image
            )
            .asImageBitmap()
    }
}