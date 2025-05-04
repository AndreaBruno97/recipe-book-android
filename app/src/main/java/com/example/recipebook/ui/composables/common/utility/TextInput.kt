package com.example.recipebook.ui.composables.common.utility

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    singleLine: Boolean = true,
    isNumeric: Boolean = false,
    isError: Boolean = false,
    supportingText: String? = null
) {
    val minLines = if (singleLine) 1 else 5
    val maxLines = if (singleLine) 1 else 10

    val keyboardOptions = if (isNumeric)
        KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    else
        KeyboardOptions.Default

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            if (labelText != null) {
                Text(labelText)
            }
        },
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
        keyboardOptions = keyboardOptions,
        isError = isError,
        supportingText = {
            val alphaValue = if (isError && supportingText != null) {
                1F
            } else {
                0F
            }
            val errorText = supportingText ?: ""

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(alphaValue)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = ""
                )
                Text(errorText)
            }
        }
    )
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeTextInputPreview() {
    RecipeBookTheme {
        TextInput(
            value = "Contenuto",
            onValueChange = {},
            labelText = "Label",
            enabled = true
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeTextInputLongPreview() {
    RecipeBookTheme {
        TextInput(
            value = "Contenuto",
            onValueChange = {},
            labelText = "Label",
            enabled = true,
            singleLine = false
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeTextInputErrorPreview() {
    RecipeBookTheme {
        TextInput(
            value = "Contenuto",
            onValueChange = {},
            labelText = "Label",
            enabled = true,
            singleLine = false,
            isError = true,
            supportingText = "Generic error"
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeTextInputErrorNoTextPreview() {
    RecipeBookTheme {
        TextInput(
            value = "Contenuto",
            onValueChange = {},
            labelText = "Label",
            enabled = true,
            singleLine = false,
            isError = true
        )
    }
}

//endregion