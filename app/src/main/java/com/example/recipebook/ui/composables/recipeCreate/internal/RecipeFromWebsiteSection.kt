package com.example.recipebook.ui.composables.recipeCreate.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.network.SupportedWebsites
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.composables.recipeCreate.RecipeWebsiteUrlErrors
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeForm_ClearWebsiteField
import com.example.recipebook.ui.theme.RecipeForm_closeRecipeFromWebsite
import com.example.recipebook.ui.theme.RecipeForm_openRecipeFromWebsite

@Composable
fun RecipeFromWebsiteSection(
    modifier: Modifier = Modifier,
    recipeWebsiteUrl: String = "",
    validateRecipeWebsiteUrl: RecipeWebsiteUrlErrors? = null,
    isRecipeFromWebsiteSectionVisible: Boolean = true,
    updateRecipeWebsiteUrl: (String) -> Unit,
    loadRecipeFromWebsite: () -> Unit,
    showRecipeFromWebsiteSection: () -> Unit,
    hideRecipeFromWebsiteSection: () -> Unit
) {
    val (toggleSectionIcon, toggleSectionAction) =
        if (isRecipeFromWebsiteSectionVisible) {
            Pair(
                RecipeForm_closeRecipeFromWebsite,
                hideRecipeFromWebsiteSection
            )
        } else {
            Pair(
                RecipeForm_openRecipeFromWebsite,
                showRecipeFromWebsiteSection
            )
        }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recipeForm_websiteUrlTitle),
                modifier = Modifier.weight(1F)
            )
            IconButton(
                onClick = toggleSectionAction
            ) {
                Icon(
                    imageVector = toggleSectionIcon,
                    contentDescription = ""
                )
            }
        }

        if (isRecipeFromWebsiteSectionVisible) {
            val supportedWebsiteList = SupportedWebsites.entries.joinToString { it.host }
            Text(
                text = stringResource(
                    R.string.recipeForm_websiteUrlWebsiteList,
                    supportedWebsiteList
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextInput(
                    value = recipeWebsiteUrl,
                    onValueChange = updateRecipeWebsiteUrl,
                    labelText = stringResource(R.string.recipeForm_websiteUrl),
                    modifier = Modifier.weight(1F),
                    enabled = true,
                    isError = validateRecipeWebsiteUrl != null && recipeWebsiteUrl.isNotBlank(),
                    supportingText = when (validateRecipeWebsiteUrl) {
                        RecipeWebsiteUrlErrors.INVALID_URL ->
                            stringResource(R.string.recipeForm_websiteUrlErrorInvalid)

                        RecipeWebsiteUrlErrors.WEBSITE_NOT_MANAGED ->
                            stringResource(R.string.recipeForm_websiteUrlErrorUnmanaged)

                        RecipeWebsiteUrlErrors.LOADING_ERROR ->
                            stringResource(R.string.recipeForm_websiteUrlLoadingError)

                        null ->
                            null
                    }
                )
                IconButton(
                    onClick = { updateRecipeWebsiteUrl("") }
                ) {
                    Icon(
                        imageVector = RecipeForm_ClearWebsiteField,
                        contentDescription = ""
                    )
                }
            }
            Button(
                onClick = loadRecipeFromWebsite,
                enabled = recipeWebsiteUrl.isNotBlank()
            ) {
                Text(stringResource(R.string.recipeForm_loadRecipeFromWebsite))
            }
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun RecipeFromWebsiteSectionPreview() {
    RecipeBookTheme {
        RecipeFromWebsiteSection(
            updateRecipeWebsiteUrl = {},
            loadRecipeFromWebsite = {},
            showRecipeFromWebsiteSection = {},
            hideRecipeFromWebsiteSection = {}
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromWebsiteSectionClosedPreview() {
    RecipeBookTheme {
        RecipeFromWebsiteSection(
            isRecipeFromWebsiteSectionVisible = false,
            updateRecipeWebsiteUrl = {},
            loadRecipeFromWebsite = {},
            showRecipeFromWebsiteSection = {},
            hideRecipeFromWebsiteSection = {}
        )
    }
}

//endregion