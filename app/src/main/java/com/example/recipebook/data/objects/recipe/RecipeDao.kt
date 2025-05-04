package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.IngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredient.IngredientItemDao
import com.example.recipebook.data.objects.ingredient.toIngredientDao
import com.example.recipebook.data.objects.ingredient.toIngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.data.objects.method.MethodDao
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.toTagDao
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

data class RecipeDao(
    val _id: ObjectId? = null,
    var name: String = "",
    var methodList: List<MethodDao> = listOf(),
    var ingredientItemList: List<IngredientItemDao> = listOf(),
    var tagList: List<TagDao> = listOf(),
    var servingsNum: String = "",
    var prepTimeMinutes: String = "",
    var cookTimeMinutes: String = "",
    var isFavorite: Boolean = false,

    // Form Validation Fields
    var validateName: Boolean = false,
    var validateServingsNum: Boolean = false,
    var validatePrepTimeMinutes: Boolean = false,
    var validateCookTimeMinutes: Boolean = false,
    var validateIsFavorite: Boolean = false,
    var validateTagList: Boolean = false,
    var validateIngredientList: Boolean = false,
    var validateMethodList: Boolean = false
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
            methodList = methodList.map { it.value }.toRealmList(),
            ingredientGroupList = ingredientGroupList.toRealmList(),
            tagList = tagList.map { it.toTag() }.toRealmList(),
            servingsNum = servingsNum.toIntOrNull(),
            prepTimeMinutes = prepTimeMinutes.toIntOrNull(),
            cookTimeMinutes = cookTimeMinutes.toIntOrNull(),
            isFavorite = isFavorite
        )
    }

    //region Input Validation

    fun isNameValid(): Boolean {
        return name.isNotBlank()
    }

    fun isServingsNumValid(): Boolean {
        return servingsNum.isBlank() || servingsNum.toIntOrNull() != null
    }

    fun isPrepTimeMinutesValid(): Boolean {
        return prepTimeMinutes.isBlank() || prepTimeMinutes.toIntOrNull() != null
    }

    fun isCookTimeMinutesValid(): Boolean {
        return cookTimeMinutes.isBlank() || cookTimeMinutes.toIntOrNull() != null
    }

    fun isIsFavoriteValid(): Boolean {
        return true
    }

    fun isMethodListValid(): Boolean {
        return methodList.isNotEmpty() &&
                methodList.all { it.validateInput() }
    }

    fun isIngredientListValid(): Boolean {
        return ingredientItemList.any { it is IngredientDao } &&
                ingredientItemList.all { it.validateInput() }
    }

    fun isTagListValid(): Boolean {
        return tagList.all { it.validateInput() }
    }

    fun validateInput(): Boolean {
        return isNameValid() &&
                isServingsNumValid() &&
                isPrepTimeMinutesValid() &&
                isCookTimeMinutesValid() &&
                isIsFavoriteValid() &&
                isMethodListValid() &&
                isIngredientListValid() &&
                isTagListValid()
    }

    fun getInputValidationCopy(): RecipeDao {
        return this.copy(
            validateName = true,
            validateServingsNum = true,
            validatePrepTimeMinutes = true,
            validateCookTimeMinutes = true,
            validateIsFavorite = true,
            validateTagList = true,
            validateIngredientList = true,
            validateMethodList = true,

            ingredientItemList = ingredientItemList.map {
                val newIngredient = when (it) {
                    is IngredientDao -> it.copy()
                    is IngredientGroupTitleDao -> it.copy()
                }

                newIngredient.enableInputValidation()

                newIngredient
            },

            methodList = methodList.map { it.getInputValidationCopy() }
        )
    }

    //endregion
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
        methodList = methodList.map { MethodDao(it) },
        ingredientItemList = ingredientItemList,
        tagList = tagList.map { it.toTagDao() },
        servingsNum = if (servingsNum == null) "" else servingsNum.toString(),
        prepTimeMinutes = if (prepTimeMinutes == null) "" else prepTimeMinutes.toString(),
        cookTimeMinutes = if (cookTimeMinutes == null) "" else cookTimeMinutes.toString(),
        isFavorite = isFavorite
    )
}