package com.example.recipebook.data.objects.ingredient

import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup

sealed interface IngredientItemDao {
    fun validateInput(): Boolean
}

//region IngredientGroupTitleDao

data class IngredientGroupTitleDao(
    var title: String? = null
) : IngredientItemDao {

    fun toIngredientGroup(): IngredientGroup = IngredientGroup(
        title = title
    )

    override fun validateInput(): Boolean {
        return true
    }
}

fun IngredientGroup.toIngredientGroupTitleDao() = IngredientGroupTitleDao(
    title = title
)

//endregion

//region IngredientDao

data class IngredientDao(
    var name: String = "",
    var quantity: String = "",
    var value: String = ""
) : IngredientItemDao {
    fun toIngredient(): Ingredient = Ingredient(
        name = name,
        quantity = quantity.toFloatOrNull(),
        value = value
    )

    override fun validateInput(): Boolean {
        return name.isNotBlank()
    }
}

fun Ingredient.toIngredientDao(): IngredientDao {
    var formattedQuantity: String = ""

    if (quantity != null) {
        formattedQuantity = if (quantity?.rem(1F) == 0F) {
            quantity?.toInt().toString()
        } else {
            quantity.toString()
        }
    }

    return IngredientDao(
        name = name,
        quantity = formattedQuantity,
        value = value
    )
}

//endregion