package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.IngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredient.IngredientItemDao
import com.example.recipebook.data.objects.ingredient.toIngredientDao
import com.example.recipebook.data.objects.ingredient.toIngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.toTagDao
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

data class RecipeDao(
    val _id: ObjectId? = null,
    val name: String = "",
    var methodList: List<String> = listOf(),
    var ingredientItemList: List<IngredientItemDao> = listOf(),
    var tagList: List<TagDao> = listOf(),
    var servingsNum: String = "",
    var prepTimeMinutes: String = "",
    var cookTimeMinutes: String = "",
    var isFavorite: Boolean = false
) {
    fun toRecipe(): Recipe {
        val ingredientGroupList: MutableList<IngredientGroup> = mutableListOf()

        if (ingredientItemList.firstOrNull() is IngredientDao) {
            // First element in list is an ingredient
            // This means that the first ingredient group has no name

            ingredientGroupList.add(IngredientGroup(title = null))
        }

        ingredientItemList.forEach { ingredientItem ->
            when (ingredientItem) {
                is IngredientDao -> ingredientGroupList.lastOrNull()?.ingredientList?.add(
                    ingredientItem.toIngredient()
                )

                is IngredientGroupTitleDao -> ingredientGroupList.add(ingredientItem.toIngredientGroup())
            }
        }

        return Recipe(
            _id = _id ?: BsonObjectId(),
            name = name,
            methodList = methodList.toRealmList(),
            ingredientGroupList = ingredientGroupList.toRealmList(),
            tagList = tagList.map { it.toTag() }.toRealmList(),
            servingsNum = servingsNum.toIntOrNull(),
            prepTimeMinutes = prepTimeMinutes.toIntOrNull(),
            cookTimeMinutes = cookTimeMinutes.toIntOrNull(),
            isFavorite = isFavorite
        )
    }

    fun validateInput(): Boolean {
        return name.isNotBlank() &&
                methodList.isNotEmpty() &&
                methodList.all { it.isNotBlank() } &&
                ingredientItemList.any { it is IngredientDao } &&
                ingredientItemList.all { it.validateInput() } &&
                tagList.all { it.validateInput() }
    }
}

fun Recipe.toRecipeDao(): RecipeDao {
    val ingredientItemList: MutableList<IngredientItemDao> = mutableListOf()

    ingredientGroupList.forEach { ingredientGroup ->
        ingredientItemList.add(
            ingredientGroup.toIngredientGroupTitleDao()
        )

        ingredientGroup.ingredientList.forEach { ingredient ->
            ingredientItemList.add(
                ingredient.toIngredientDao()
            )
        }
    }

    return RecipeDao(
        _id = _id,
        name = name,
        methodList = methodList,
        ingredientItemList = ingredientItemList,
        tagList = tagList.map { it.toTagDao() },
        servingsNum = if (servingsNum == null) "" else servingsNum.toString(),
        prepTimeMinutes = if (prepTimeMinutes == null) "" else prepTimeMinutes.toString(),
        cookTimeMinutes = if (cookTimeMinutes == null) "" else cookTimeMinutes.toString(),
        isFavorite = isFavorite
    )
}