package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.toIngredientDao
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.toTagDao
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

data class RecipeDao(
    val _id: ObjectId? = null,
    val name: String = "",
    var methodList: List<String> = listOf(),
    var ingredients: List<IngredientDao> = listOf(),
    var tags: List<TagDao> = listOf()
) {
    fun toRecipe(): Recipe = Recipe(
        _id = _id ?: BsonObjectId(),
        name = name,
        methodList = methodList.toRealmList(),
        ingredients = ingredients.map { it.toIngredient() }.toRealmList(),
        tags = tags.map { it.toTag() }.toRealmList()
    )

    fun validateInput(): Boolean {
        return name.isNotBlank() &&
                methodList.isNotEmpty() &&
                methodList.all { it.isNotBlank() } &&
                ingredients.isNotEmpty() &&
                ingredients.all { it.validateInput() } &&
                tags.all { it.validateInput() }
    }
}

fun Recipe.toRecipeDao(): RecipeDao = RecipeDao(
    _id = _id,
    name = name,
    methodList = methodList,
    ingredients = ingredients.map { it.toIngredientDao() },
    tags = tags.map { it.toTagDao() }
)