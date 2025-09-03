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

enum class WarningDialogType {
    DELETE, OVERRIDE
}

@Composable
fun WarningConfirmationDialog(
    isPopupOpen: Boolean,
    text: String,
    warningType: WarningDialogType,
    onWarningConfirm: () -> Unit,
    onWarningCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val confirmMessage = when (warningType) {
        WarningDialogType.DELETE -> stringResource(R.string.warningDialog_confirmationButton_confirm_delete)
        WarningDialogType.OVERRIDE -> stringResource(R.string.warningDialog_confirmationButton_confirm_override)
    }

    if (isPopupOpen) {
        AlertDialog(
            onDismissRequest = onWarningCancel,
            title = { Text(stringResource(R.string.deletePopup_title)) },
            text = { Text(text) },
            modifier = modifier,
            dismissButton = {
                TextButton(onClick = onWarningCancel) {
                    Text(stringResource(R.string.warningDialog_confirmationButton_cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = onWarningConfirm) {
                    Text(confirmMessage)
                }
            }
        )
    }
}

//region Preview

@DefaultPreview
@Composable
private fun WarningConfirmationDialogDeletePreview() {
    RecipeBookTheme {
        WarningConfirmationDialog(
            isPopupOpen = true,
            text = "Lorem ipsum",
            warningType = WarningDialogType.DELETE,
            onWarningConfirm = {},
            onWarningCancel = {}
        )
    }
}

@DefaultPreview
@Composable
private fun WarningConfirmationDialogOverridePreview() {
    RecipeBookTheme {
        WarningConfirmationDialog(
            isPopupOpen = true,
            text = "Lorem ipsum",
            warningType = WarningDialogType.OVERRIDE,
            onWarningConfirm = {},
            onWarningCancel = {}
        )
    }
}

//endregion