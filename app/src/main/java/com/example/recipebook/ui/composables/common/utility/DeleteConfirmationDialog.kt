package com.example.recipebook.ui.composables.common.utility

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun DeleteConfirmationDialog(
    isPopupOpen: Boolean,
    text: String,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isPopupOpen) {
        AlertDialog(
            onDismissRequest = onDeleteCancel,
            title = { Text(stringResource(R.string.deletePopup_title)) },
            text = { Text(text) },
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
}

//region Preview

@DefaultPreview
@Composable
private fun DeleteConfirmationDialogPreview() {
    RecipeBookTheme {
        DeleteConfirmationDialog(
            isPopupOpen = true,
            text = "Lorem ipsum",
            onDeleteConfirm = {},
            onDeleteCancel = {}
        )
    }
}

//endregion