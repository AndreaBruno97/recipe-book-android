package com.example.recipebook.data.objects.ingredient

class IngredientExamples {
    companion object{
        val ingredientA: Ingredient = Ingredient(
            name = "Ingrediente A",
            value = "AAA"
        )

        val ingredientB: Ingredient = Ingredient(
            name = "Ingrediente B",
            value = "BBB"
        )

        val ingredientC: Ingredient = Ingredient(
            name = "Ingrediente C",
            value = "CCC"
        )

        val ingredientD: Ingredient = Ingredient(
            name = "Ingrediente D",
            value = "DDD"
        )

        val ingredientList: List<Ingredient> = listOf(ingredientA, ingredientB, ingredientC, ingredientD)
    }
}