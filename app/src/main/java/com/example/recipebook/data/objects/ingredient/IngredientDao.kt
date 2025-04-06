package com.example.recipebook.data.objects.ingredient

data class IngredientDao(
    var name: String = "",
    var quantity: String = "",
    var value: String = ""
) {
    fun toIngredient(): Ingredient = Ingredient(
        name = name,
        quantity = quantity.toFloatOrNull(),
        value = value
    )

    fun validateInput(): Boolean {
        return name.isNotBlank() &&
                value.isNotBlank()
    }
}

fun Ingredient.toIngredientDao(): IngredientDao {
    var formattedQuantity: String = ""

    if (quantity != null) {
        if (quantity?.rem(1F) == 0F) {
            formattedQuantity = quantity?.toInt().toString()
        } else {
            formattedQuantity = quantity.toString()
        }
    }

    return IngredientDao(
        name = name,
        quantity = formattedQuantity,
        value = value
    )
}