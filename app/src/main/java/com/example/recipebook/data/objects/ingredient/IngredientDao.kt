package com.example.recipebook.data.objects.ingredient

data class IngredientDao(
    var name: String = "",
    var value: String = ""
) {
    fun toIngredient(): Ingredient = Ingredient(
        name = name,
        value = value
    )

    fun validateInput(): Boolean {
        return name.isNotBlank() &&
                value.isNotBlank()
    }
}

fun Ingredient.toIngredientDao(): IngredientDao = IngredientDao(
    name = name,
    value = value
)