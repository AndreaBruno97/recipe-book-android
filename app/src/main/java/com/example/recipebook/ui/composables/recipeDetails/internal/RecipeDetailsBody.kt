package com.example.recipebook.ui.composables.recipeDetails.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsUiState
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import io.realm.kotlin.ext.realmListOf
import java.math.RoundingMode

@Composable
fun RecipeDetailsBody(
    recipeDetailsUiState: RecipeDetailsUiState,
    recipeImage: ImageBitmap? = null,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isDeletePopupOpen: Boolean = false,
    openDeletePopup: () -> Unit,
    closeDeletePopup: () -> Unit
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeDetails(
            recipe = recipeDetailsUiState.recipe,
            recipeImage = recipeImage,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = openDeletePopup,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
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
    recipe: Recipe, modifier: Modifier = Modifier,
    recipeImage: ImageBitmap? = null
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
            Text(if (recipe.servingsNum == null) "-" else recipe.servingsNum.toString())
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

        Row {
            Text(
                stringResource(R.string.recipe_isFavorite) + ": ",
                style = MaterialTheme.typography.titleMedium
            )
            Text(recipe.isFavorite.toString())
        }

        Text(stringResource(R.string.recipe_tags), style = MaterialTheme.typography.titleMedium)
        Text(recipe.tagList.joinToString(separator = ", ", transform = { it.name }))

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
                val quantity = ingredient.quantity

                if (quantity != null) {
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
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeDetailsBodyEmptyFieldsPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            RecipeDetailsUiState(RecipeExamples.recipe1.apply {
                servingsNum = null
                prepTimeMinutes = null
                cookTimeMinutes = null
                isFavorite = false
            }),
            recipeImage = RecipeExamples.recipeImageBitmap,
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

@DefaultPreview
@Composable
fun IngredientQuantityFormattingPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            RecipeDetailsUiState(RecipeExamples.recipe1.apply {
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
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

//endregion