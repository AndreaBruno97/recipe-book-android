package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun RecipeImageInput(
    recipeImage: ImageBitmap?,
    onTakeImage: () -> Unit,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (recipeImage != null) {
            Image(
                bitmap = recipeImage,
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.height(100.dp)
            )
        }

        Button(
            onClick = onTakeImage,
            enabled = enabled
        ) {
            Text(stringResource(id = R.string.recipe_takeImage))
        }

        Button(
            onClick = onPickImage,
            enabled = enabled
        ) {
            Text(stringResource(id = R.string.recipe_pickImage))
        }

        Button(
            onClick = onClearImage,
            enabled = enabled && recipeImage != null
        ) {
            Text(stringResource(id = R.string.recipe_clearImage))
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeImageInputPreview() {
    RecipeBookTheme {
        RecipeImageInput(
            recipeImage = RecipeExamples.recipeImageBitmap,
            onTakeImage = {},
            onPickImage = {},
            onClearImage = {},
            enabled = true
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeImageInputNoImagePreview() {
    RecipeBookTheme {
        RecipeImageInput(
            recipeImage = null,
            onTakeImage = {},
            onPickImage = {},
            onClearImage = {},
            enabled = true
        )
    }
}

//endregion