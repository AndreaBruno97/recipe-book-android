package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.IngredientExamples
import com.example.recipebook.data.objects.ingredient.toIngredientDao
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.ui.composables.common.utility.SortableList
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeForm_AddIngredient


@Composable
fun RecipeIngredientsInput(
    recipeDao: RecipeDao,
    onValueChange: (RecipeDao) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(stringResource(R.string.recipe_ingredients) + "*")

        SortableList<IngredientDao>(
            itemList = recipeDao.ingredients,
            updateList = { ingredientList ->
                onValueChange(recipeDao.copy(ingredients = ingredientList))
            },
            onClickNewItem = {
                onValueChange(
                    recipeDao.copy(
                        ingredients =
                        recipeDao.ingredients.plus(listOf(IngredientDao()))
                    )
                )
            },
            newItemButtonIcon = RecipeForm_AddIngredient,
            newItemButtonText = R.string.recipeForm_addIngredient,
            enabled = enabled
        ) { ingredient, index, modifier ->
            IngredientInputLine(
                ingredient = ingredient,
                onNameChange = { newName ->
                    val newIngredientList = recipeDao.ingredients.map { it.copy() }
                    newIngredientList[index].name = newName
                    onValueChange(recipeDao.copy(ingredients = newIngredientList))
                },
                onQuantityChange = { newQuantity ->
                    val newIngredientList = recipeDao.ingredients.map { it.copy() }
                    newIngredientList[index].quantity = newQuantity
                    onValueChange(recipeDao.copy(ingredients = newIngredientList))
                },
                onValueChange = { newValue ->
                    val newIngredientList = recipeDao.ingredients.map { it.copy() }
                    newIngredientList[index].value = newValue
                    onValueChange(recipeDao.copy(ingredients = newIngredientList))
                },
                enabled = enabled,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun IngredientInputLine(
    ingredient: IngredientDao,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = ingredient.name,
            onValueChange = onNameChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 4F),
            maxLines = 1
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
        TextField(
            value = ingredient.quantity,
            onValueChange = onQuantityChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            enabled = enabled,
            modifier = Modifier.weight(weight = 3F),
            maxLines = 1
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
        TextField(
            value = ingredient.value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 3F),
            maxLines = 1
        )
    }
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeIngredientsInputPreview() {
    RecipeBookTheme {
        RecipeIngredientsInput(
            recipeDao = RecipeExamples.recipe1.toRecipeDao(),
            onValueChange = {},
            enabled = true,
        )
    }
}

@DefaultPreview
@Composable
private fun IngredientInputLinePreview() {
    RecipeBookTheme {
        IngredientInputLine(
            ingredient = IngredientExamples.ingredientA.toIngredientDao(),
            onNameChange = {},
            onQuantityChange = {},
            onValueChange = {},
            enabled = true
        )
    }
}

//endregion