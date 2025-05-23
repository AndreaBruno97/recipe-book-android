@file:OptIn(ExperimentalLayoutApi::class)

package com.example.recipebook.ui.composables.recipeDetails.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsUiState
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeDetails_ResetServingsNum
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import java.math.RoundingMode

@Composable
fun RecipeDetailsBody(
    recipeDetailsUiState: RecipeDetailsUiState,
    modifier: Modifier = Modifier,
    recipeImage: ImageBitmap? = null,
    curServingsNum: Int? = null,
    isDeletePopupOpen: Boolean = false,
    curIsFavorite: Boolean = true,
    enabled: Boolean = true,
    servingsRatio: Float? = null,
    openDeletePopup: () -> Unit,
    onDelete: () -> Unit,
    closeDeletePopup: () -> Unit,
    increaseServingsNum: () -> Unit,
    decreaseServingsNum: () -> Unit,
    resetServingsNum: () -> Unit,
    toggleIsFavorite: () -> Unit
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeDetails(
            recipe = recipeDetailsUiState.recipe,
            modifier = Modifier.fillMaxWidth(),
            recipeImage = recipeImage,
            curServingsNum = curServingsNum,
            servingsRatio = servingsRatio,
            curIsFavorite = curIsFavorite,
            enabled = enabled,
            increaseServingsNum = increaseServingsNum,
            decreaseServingsNum = decreaseServingsNum,
            resetServingsNum = resetServingsNum,
            toggleIsFavorite = toggleIsFavorite
        )
        OutlinedButton(
            onClick = openDeletePopup,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Text(stringResource(R.string.delete_button_text))
        }

        if (isDeletePopupOpen) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    closeDeletePopup()
                    onDelete()
                },
                onDeleteCancel = closeDeletePopup,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
private fun RecipeDetails(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    recipeImage: ImageBitmap? = null,
    curServingsNum: Int? = null,
    servingsRatio: Float? = null,
    curIsFavorite: Boolean = false,
    enabled: Boolean = true,
    increaseServingsNum: () -> Unit,
    decreaseServingsNum: () -> Unit,
    resetServingsNum: () -> Unit,
    toggleIsFavorite: () -> Unit
) {
    Column(modifier = modifier) {
        Text(recipe.name, style = MaterialTheme.typography.titleLarge)

        if (recipeImage != null) {
            Image(
                bitmap = recipeImage,
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.height(100.dp)
            )
        }

        Row {
            Text(
                stringResource(R.string.recipe_servingsNum) + ": ",
                style = MaterialTheme.typography.titleMedium
            )
            Text(curServingsNum?.toString() ?: "-")
            Button(
                onClick = decreaseServingsNum,
                enabled = enabled && curServingsNum != null && curServingsNum > 1
            ) {
                Text("-")
            }
            Button(
                onClick = increaseServingsNum,
                enabled = enabled && curServingsNum != null
            ) {
                Text("+")
            }
            IconButton(
                onClick = resetServingsNum,
                enabled = enabled && curServingsNum != null && curServingsNum != recipe.servingsNum
            ) {
                Icon(imageVector = RecipeDetails_ResetServingsNum, contentDescription = "")
            }
        }

        Row {
            Text(
                stringResource(R.string.recipe_prepTimeMinutes) + ": ",
                style = MaterialTheme.typography.titleMedium
            )
            Text(if (recipe.prepTimeMinutes == null) "-" else recipe.prepTimeMinutes.toString())
        }

        Row {
            Text(
                stringResource(R.string.recipe_cookTimeMinutes) + ": ",
                style = MaterialTheme.typography.titleMedium
            )
            Text(if (recipe.cookTimeMinutes == null) "-" else recipe.cookTimeMinutes.toString())
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.recipe_isFavorite) + ": ",
                style = MaterialTheme.typography.titleMedium
            )
            Text(curIsFavorite.toString())
            RadioButton(
                selected = curIsFavorite,
                onClick = toggleIsFavorite,
                enabled = enabled
            )
        }

        Text(stringResource(R.string.recipe_tags), style = MaterialTheme.typography.titleMedium)

        FlowRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (tag in recipe.tagList) {
                val tagName = if (tag.icon != null) {
                    "${tag.icon} ${tag.name}"
                } else {
                    tag.name
                }

                Text(
                    text = tagName,
                    color = tag.colorObj,
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
                )
            }
        }

        Text(
            stringResource(R.string.recipe_ingredients),
            style = MaterialTheme.typography.titleMedium
        )

        for ((index, ingredientGroup) in recipe.ingredientGroupList.withIndex()) {
            val title = ingredientGroup.title

            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleSmall)
            }

            for (ingredient in ingredientGroup.ingredientList) {
                var quantityString = ""
                var quantity = ingredient.quantity

                if (quantity != null) {
                    if (servingsRatio != null) {
                        quantity *= servingsRatio
                    }

                    val formattedQuantity = quantity
                        .toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                        .toPlainString()

                    quantityString = "${formattedQuantity} "
                }

                Text("${ingredient.name}: ${quantityString}${ingredient.value}")
            }

            if (index < recipe.ingredientGroupList.size - 1) {
                HorizontalDivider()
            }
        }

        Text(stringResource(R.string.recipe_method), style = MaterialTheme.typography.titleMedium)

        for ((index, method) in recipe.methodList.withIndex()) {
            Text(method)
            if (index < recipe.methodList.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = { Text(stringResource(R.string.deletePopup_title)) },
        text = { Text(stringResource(R.string.deletePopup_text)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.confirmationButton_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.confirmationButton_confirm))
            }
        }

    )
}

//region Preview

@DefaultPreview
@Composable
fun RecipeDetailsBodyPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            RecipeDetailsUiState(RecipeExamples.recipe1),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeDetailsBodyEmptyFieldsPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            RecipeDetailsUiState(RecipeExamples.recipe1.toRecipeDao().toRecipe().apply {
                servingsNum = null
                prepTimeMinutes = null
                cookTimeMinutes = null
                isFavorite = false
                tagList = TagExamples.tagList.plus(TagExamples.tagList).toRealmList()
            }),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {}
        )
    }
}

@DefaultPreview
@Composable
fun IngredientQuantityFormattingPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            RecipeDetailsUiState(RecipeExamples.recipe1.toRecipeDao().toRecipe().apply {
                ingredientGroupList = realmListOf(
                    IngredientGroup(
                        "Test decimali",
                        ingredientList = realmListOf(
                            Ingredient("a", 1F, "a"),
                            Ingredient("a", 12F, "a"),
                            Ingredient("a", 123F, "a"),
                            Ingredient("a", 1.016F, "a"),
                            Ingredient("a", 320F, "a"),
                            Ingredient("a", 21.010101F, "a"),
                            Ingredient("a", 1231.010000F, "a"),
                            Ingredient("a", 10.001002F, "a")
                        )
                    )
                )
            }),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = 4,
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {}
        )
    }
}

//endregion