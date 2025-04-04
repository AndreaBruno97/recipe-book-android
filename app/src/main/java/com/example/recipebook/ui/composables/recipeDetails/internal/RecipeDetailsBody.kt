package com.example.recipebook.ui.composables.recipeDetails.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsUiState
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun RecipeDetailsBody(
    recipeDetailsUiState: RecipeDetailsUiState,
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
    recipe: Recipe, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(recipe.name, style = MaterialTheme.typography.titleLarge)

        Text(stringResource(R.string.recipe_tags), style = MaterialTheme.typography.titleMedium)
        Text(recipe.tags.joinToString(separator = ", ", transform = { it.name }))

        Text(
            stringResource(R.string.recipe_ingredients),
            style = MaterialTheme.typography.titleMedium
        )
        for (ingredient in recipe.ingredients) {
            Text("${ingredient.name}: ${ingredient.value}")
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
            onDelete = {},
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

//endregion