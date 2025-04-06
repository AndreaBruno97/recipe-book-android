package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun RecipeTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    labelText: String = "",
    singleLine: Boolean = true,
    isNumeric: Boolean = false
) {
    val minLines = if (singleLine) 1 else 5
    val maxLines = if (singleLine) 2 else 10

    val keyboardOptions = if (isNumeric)
        KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    else
        KeyboardOptions.Default

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions
    )
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeTextInputPreview() {
    RecipeBookTheme {
        RecipeTextInput(
            value = "Contenuto",
            onValueChange = {},
            labelText = "Label",
            enabled = true
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeTextInputoLongPreview() {
    RecipeBookTheme {
        RecipeTextInput(
            value = "Contenuto",
            onValueChange = {},
            labelText = "Label",
            enabled = true,
            singleLine = false
        )
    }
}

//endregion