package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.IngredientExamples
import com.example.recipebook.data.objects.ingredient.IngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredient.IngredientItemDao
import com.example.recipebook.data.objects.ingredient.toIngredientDao
import com.example.recipebook.data.objects.ingredient.toIngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroupExamples
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

        val newIngredientItemList = recipeDao.ingredientItemList.map {
            when (it) {
                is IngredientDao -> it.copy()
                is IngredientGroupTitleDao -> it.copy()
            }
        }

        SortableList<IngredientItemDao>(
            itemList = recipeDao.ingredientItemList,
            updateList = { ingredientItemList ->
                onValueChange(recipeDao.copy(ingredientItemList = ingredientItemList))
            },
            onClickNewItem = null,
            newItemButtonIcon = null,
            newItemButtonText = null,
            enabled = enabled
        ) { ingredientItem, index, modifier ->
            val newIngredientItem = newIngredientItemList[index]

            when (ingredientItem) {
                is IngredientDao -> IngredientInputLine(
                    ingredient = ingredientItem,
                    onNameChange = { newName ->
                        if (newIngredientItem is IngredientDao) {
                            newIngredientItem.name = newName
                        }
                        onValueChange(recipeDao.copy(ingredientItemList = newIngredientItemList))
                    },
                    onQuantityChange = { newQuantity ->
                        if (newIngredientItem is IngredientDao) {
                            newIngredientItem.quantity = newQuantity
                        }
                        onValueChange(recipeDao.copy(ingredientItemList = newIngredientItemList))
                    },
                    onValueChange = { newValue ->
                        if (newIngredientItem is IngredientDao) {
                            newIngredientItem.value = newValue
                        }
                        onValueChange(recipeDao.copy(ingredientItemList = newIngredientItemList))
                    },
                    enabled = enabled,
                    modifier = modifier
                )

                is IngredientGroupTitleDao -> IngredientGroupTitleInputLine(
                    ingredientGroupTitle = ingredientItem,
                    onTitleChange = { newTitle ->
                        if (newIngredientItem is IngredientGroupTitleDao) {
                            newIngredientItem.title = newTitle
                        }
                        onValueChange(recipeDao.copy(ingredientItemList = newIngredientItemList))
                    },
                    modifier = modifier,
                    enabled = enabled
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    onValueChange(
                        recipeDao.copy(
                            ingredientItemList =
                            recipeDao.ingredientItemList.plus(listOf(IngredientDao()))
                        )
                    )
                },
                modifier = Modifier.weight(1F)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(imageVector = RecipeForm_AddIngredient, contentDescription = "")
                    Text(
                        text = stringResource(id = R.string.recipeForm_addIngredient),
                        modifier = Modifier.weight(1F)
                    )
                }
            }
            Button(
                onClick = {
                    onValueChange(
                        recipeDao.copy(
                            ingredientItemList =
                            recipeDao.ingredientItemList.plus(listOf(IngredientGroupTitleDao()))
                        )
                    )
                },
                modifier = Modifier.weight(1F)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(imageVector = RecipeForm_AddIngredient, contentDescription = "")
                    Text(
                        text = stringResource(id = R.string.recipeForm_addIngredientGroup),
                        modifier = Modifier.weight(1F)
                    )
                }
            }
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

@Composable
private fun IngredientGroupTitleInputLine(
    ingredientGroupTitle: IngredientGroupTitleDao,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = ingredientGroupTitle.title ?: "",
            onValueChange = onTitleChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 1F),
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

@DefaultPreview
@Composable
private fun IngredientGroupTitleInputLinePreview() {
    RecipeBookTheme {
        IngredientGroupTitleInputLine(
            ingredientGroupTitle = IngredientGroupExamples.ingredientGroupA.toIngredientGroupTitleDao(),
            onTitleChange = {},
            enabled = true
        )
    }
}

//endregion