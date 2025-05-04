package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.example.recipebook.ui.composables.common.utility.TextInput
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

        if (
            recipeDao.validateIngredientList &&
            recipeDao.ingredientItemList.isNotEmpty() &&
            recipeDao.ingredientItemList.none { it is IngredientDao }
        ) {
            // The list is not empty, but there are no actual ingredients

            Text(
                stringResource(id = R.string.formError_ingredientList_NoIngredients),
                color = Color.Red
            )
        }

        SortableList<IngredientItemDao>(
            itemList = recipeDao.ingredientItemList,
            updateList = { ingredientItemList ->
                onValueChange(
                    recipeDao
                        .copy(
                            ingredientItemList = ingredientItemList,
                            validateIngredientList = false
                        )
                )
            },
            onClickNewItem = null,
            newItemButtonIcon = null,
            newItemButtonText = null,
            enabled = enabled,
            showEmptyError = recipeDao.validateIngredientList
        ) { ingredientItem, index, modifier ->
            val newIngredientItem = newIngredientItemList[index]

            when (ingredientItem) {
                is IngredientDao -> IngredientInputLine(
                    ingredient = ingredientItem,
                    onNameChange = { newName ->
                        if (newIngredientItem is IngredientDao) {
                            newIngredientItem.name = newName
                            newIngredientItem.validateName = false
                        }
                        onValueChange(recipeDao.copy(ingredientItemList = newIngredientItemList))
                    },
                    onQuantityChange = { newQuantity ->
                        if (newIngredientItem is IngredientDao) {
                            newIngredientItem.quantity = newQuantity
                            newIngredientItem.validateQuantity = false
                        }
                        onValueChange(recipeDao.copy(ingredientItemList = newIngredientItemList))
                    },
                    onValueChange = { newValue ->
                        if (newIngredientItem is IngredientDao) {
                            newIngredientItem.value = newValue
                            newIngredientItem.validateValue = false
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
                            newIngredientItem.validateTitle = false
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
                        recipeDao
                            .copy(
                                ingredientItemList =
                                recipeDao.ingredientItemList.plus(listOf(IngredientDao())),
                                validateIngredientList = false
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
                        recipeDao
                            .copy(
                                ingredientItemList =
                                recipeDao.ingredientItemList.plus(listOf(IngredientGroupTitleDao())),
                                validateIngredientList = false
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
        TextInput(
            value = ingredient.name,
            onValueChange = onNameChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 4F),
            singleLine = true,
            isError = ingredient.validateName && !ingredient.isNameValid(),
            supportingText = stringResource(R.string.formError_mandatoryField)
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
        TextInput(
            value = ingredient.quantity,
            onValueChange = onQuantityChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 3F),
            singleLine = true,
            isNumeric = true,
            isError = ingredient.validateQuantity && !ingredient.isQuantityValid(),
            supportingText = stringResource(R.string.formError_numericField)
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
        TextInput(
            value = ingredient.value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 3F),
            singleLine = true,
            isError = ingredient.validateValue && !ingredient.isValueValid(),
            supportingText = null
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
        TextInput(
            value = ingredientGroupTitle.title ?: "",
            onValueChange = onTitleChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 1F),
            singleLine = true,
            isError = ingredientGroupTitle.validateTitle && !ingredientGroupTitle.isTitleValid(),
            supportingText = null
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
private fun RecipeIngredientsInputNoIngredientPreview() {
    RecipeBookTheme {
        RecipeIngredientsInput(
            recipeDao = RecipeExamples.recipe1.toRecipeDao()
                .copy(
                    ingredientItemList = listOf(IngredientGroupTitleDao()),
                    validateIngredientList = true
                ),
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
private fun IngredientInputLineErrorPreview() {
    RecipeBookTheme {
        IngredientInputLine(
            ingredient = IngredientExamples.ingredientA.toIngredientDao()
                .copy(
                    name = "",
                    quantity = "a",
                    value = "",

                    validateName = true,
                    validateQuantity = true,
                    validateValue = true
                ),
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