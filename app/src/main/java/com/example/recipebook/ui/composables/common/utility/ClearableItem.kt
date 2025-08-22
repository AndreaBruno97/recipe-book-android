package com.example.recipebook.ui.composables.common.utility

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.recipebook.R
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.ClearableItem_Clear
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun ClearableItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    clearItem: () -> Unit,
    content: @Composable (modifier: Modifier) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        content(Modifier.weight(1F))

        IconButton(
            onClick = clearItem,
            enabled = enabled
        ) {
            Icon(
                imageVector = ClearableItem_Clear,
                contentDescription = ""
            )
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun ClearableItemPreview() {
    RecipeBookTheme {
        ClearableItem(
            clearItem = {}
        ){
            Text("Test")
        }
    }
}

@DefaultPreview
@Composable
private fun ClearableItemDisabledPreview() {
    RecipeBookTheme {
        ClearableItem(
            clearItem = {},
            enabled = false
        ){
            Text("Test")
        }
    }
}

//endregion