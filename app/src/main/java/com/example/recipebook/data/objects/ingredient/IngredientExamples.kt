package com.example.recipebook.data.objects.ingredient

class IngredientExamples {
    companion object {
        val ingredientA: Ingredient = Ingredient(
            name = "Ingrediente A",
            quantity = 1F,
            value = "AAA"
        )

        val ingredientB: Ingredient = Ingredient(
            name = "Ingrediente B",
            quantity = 2F,
            value = "BBB"
        )

        val ingredientC: Ingredient = Ingredient(
            name = "Ingrediente C",
            quantity = 3F,
            value = "CCC"
        )

        val ingredientD: Ingredient = Ingredient(
            name = "Ingrediente D",
            quantity = 4F,
            value = "DDD"
        )

        val ingredientList: List<Ingredient> =
            listOf(ingredientA, ingredientB, ingredientC, ingredientD)
    }
}