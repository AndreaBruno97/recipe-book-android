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
import com.example.recipebook.ui.composables.common.utility.ClearableItem
import com.example.recipebook.ui.composables.common.utility.CollapsableSection
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.composables.recipeCreate.RecipeWebsiteUrlErrors
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.RecipeBookTheme

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
    val supportedWebsiteList = SupportedWebsites.entries.joinToString { it.host }
    val supportingText = when (validateRecipeWebsiteUrl) {
        RecipeWebsiteUrlErrors.INVALID_URL ->
            stringResource(R.string.recipeForm_websiteUrlErrorInvalid)

        RecipeWebsiteUrlErrors.WEBSITE_NOT_MANAGED ->
            stringResource(R.string.recipeForm_websiteUrlErrorUnmanaged)

        RecipeWebsiteUrlErrors.LOADING_ERROR ->
            stringResource(R.string.recipeForm_websiteUrlLoadingError)

        null ->
            null
    }

    CollapsableSection(
        isCollapsed = (isRecipeFromWebsiteSectionVisible == false),
        title = stringResource(R.string.recipeForm_websiteUrlTitle),
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        collapseSection = hideRecipeFromWebsiteSection,
        expandSection = showRecipeFromWebsiteSection
    ) {
        Text(
            text = stringResource(
                R.string.recipeForm_websiteUrlWebsiteList,
                supportedWebsiteList
            )
        )

        ClearableItem(
            modifier = Modifier.fillMaxWidth(),
            clearItem = { updateRecipeWebsiteUrl("") }
        ) { clearableItemModifier ->
            TextInput(
                value = recipeWebsiteUrl,
                onValueChange = updateRecipeWebsiteUrl,
                labelText = stringResource(R.string.recipeForm_websiteUrl),
                modifier = clearableItemModifier,
                enabled = true,
                isError = validateRecipeWebsiteUrl != null && recipeWebsiteUrl.isNotBlank(),
                supportingText = supportingText
            )
        }

        Button(
            onClick = loadRecipeFromWebsite,
            enabled = recipeWebsiteUrl.isNotBlank()
        ) {
            Text(stringResource(R.string.recipeForm_loadRecipeFromWebsite))
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