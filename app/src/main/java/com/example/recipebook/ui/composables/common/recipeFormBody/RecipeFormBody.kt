package com.example.recipebook.ui.composables.common.recipeFormBody

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeIngredientsInput
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeMethodListInput
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeTagsInput
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeTextInput
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun RecipeFormBody(
    recipeUiState: RecipeUiState,
    modifier: Modifier = Modifier,
    onRecipeValueChange: (RecipeDao) -> Unit,
    onSaveClick: () -> Unit,
    unusedTagList: List<Tag>,
    openTagListPopup: () -> Unit,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
) {
    val recipeDao = recipeUiState.recipeDao

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeInputForm(
            recipeDao = recipeDao,
            onValueChange = onRecipeValueChange,
            modifier = Modifier.fillMaxWidth(),
            openTagListPopup = openTagListPopup,
            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen
        )
        Button(
            onClick = onSaveClick,
            enabled = recipeUiState.recipeDao.validateInput(),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_button_label))
        }
    }
}

@Composable
fun RecipeInputForm(
    recipeDao: RecipeDao,
    modifier: Modifier = Modifier,
    onValueChange: (RecipeDao) -> Unit = {},
    enabled: Boolean = true,
    openTagListPopup: () -> Unit,
    unusedTagList: List<Tag>,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeTextInput(
            value = recipeDao.name,
            onValueChange = { onValueChange(recipeDao.copy(name = it)) },
            labelText = stringResource(R.string.recipe_name) + "*",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        RecipeTextInput(
            value = recipeDao.servingsNum,
            onValueChange = { onValueChange(recipeDao.copy(servingsNum = it)) },
            labelText = stringResource(R.string.recipe_servingsNum),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isNumeric = true
        )

        RecipeTextInput(
            value = recipeDao.prepTimeMinutes,
            onValueChange = { onValueChange(recipeDao.copy(prepTimeMinutes = it)) },
            labelText = stringResource(R.string.recipe_prepTimeMinutes),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isNumeric = true
        )

        RecipeTextInput(
            value = recipeDao.cookTimeMinutes,
            onValueChange = { onValueChange(recipeDao.copy(cookTimeMinutes = it)) },
            labelText = stringResource(R.string.recipe_cookTimeMinutes),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isNumeric = true
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.recipe_isFavorite))
            Checkbox(
                checked = recipeDao.isFavorite,
                onCheckedChange = { onValueChange(recipeDao.copy(isFavorite = it)) }
            )
        }

        RecipeTagsInput(
            recipeDao = recipeDao,
            onValueChange = onValueChange,
            enabled = enabled,
            openTagListPopup = openTagListPopup,
            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen
        )

        RecipeIngredientsInput(
            recipeDao = recipeDao,
            onValueChange = onValueChange,
            enabled = enabled
        )

        RecipeMethodListInput(
            recipeDao = recipeDao,
            onValueChange = onValueChange,
            enabled = enabled
        )

        if (enabled) {
            Text(
                text = stringResource(R.string.formLegend_mandatoryFields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeFormBodyScreenPreview() {
    RecipeBookTheme {
        RecipeFormBody(
            recipeUiState = RecipeUiState(
                RecipeExamples.recipe1.toRecipeDao()
            ),
            onRecipeValueChange = {},
            onSaveClick = {},
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeFormBodyScreenWithPopupPreview() {
    RecipeBookTheme {
        RecipeFormBody(
            recipeUiState = RecipeUiState(
                RecipeExamples.recipe1.toRecipeDao()
            ),
            onRecipeValueChange = {},
            onSaveClick = {},
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {},
            isTagListPopupOpen = true
        )
    }
}

//endregion