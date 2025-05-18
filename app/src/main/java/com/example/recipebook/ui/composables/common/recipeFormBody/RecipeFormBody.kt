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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.IngredientGroupTitleDao
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeImageInput
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeIngredientsInput
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeMethodListInput
import com.example.recipebook.ui.composables.common.recipeFormBody.internal.RecipeTagsInput
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun RecipeFormBody(
    recipeUiState: RecipeUiState,
    modifier: Modifier = Modifier,
    onRecipeValueChange: (RecipeDao) -> Unit,
    onSaveClick: () -> Unit,
    onTakeImage: () -> Unit,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit,
    unusedTagList: List<Tag>,
    openTagListPopup: () -> Unit,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    recipeImage: ImageBitmap?,
    tagListFilterName: String = "",
    tagListUpdateFilterName: (String) -> Unit
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
            onTakeImage = onTakeImage,
            onPickImage = onPickImage,
            onClearImage = onClearImage,
            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen,
            recipeImage = recipeImage,
            tagListFilterName = tagListFilterName,
            tagListUpdateFilterName = tagListUpdateFilterName
        )
        Button(
            onClick = onSaveClick,
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
    onTakeImage: () -> Unit,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit,
    unusedTagList: List<Tag>,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    recipeImage: ImageBitmap?,
    tagListFilterName: String = "",
    tagListUpdateFilterName: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        TextInput(
            value = recipeDao.name,
            onValueChange = {
                onValueChange(
                    recipeDao
                        .copy(
                            name = it,
                            validateName = false
                        )
                )
            },
            labelText = stringResource(R.string.recipe_name) + "*",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isError = recipeDao.validateName && !recipeDao.isNameValid(),
            supportingText = stringResource(R.string.formError_mandatoryField)
        )

        RecipeImageInput(
            recipeImage = recipeImage,
            onTakeImage = onTakeImage,
            onPickImage = onPickImage,
            onClearImage = onClearImage,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        TextInput(
            value = recipeDao.servingsNum,
            onValueChange = {
                onValueChange(
                    recipeDao
                        .copy(
                            servingsNum = it,
                            validateServingsNum = false
                        )
                )
            },
            labelText = stringResource(R.string.recipe_servingsNum),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isNumeric = true,
            isError = recipeDao.validateServingsNum && !recipeDao.isServingsNumValid(),
            supportingText = stringResource(R.string.formError_integerField)
        )

        TextInput(
            value = recipeDao.prepTimeMinutes,
            onValueChange = {
                onValueChange(
                    recipeDao
                        .copy(
                            prepTimeMinutes = it,
                            validatePrepTimeMinutes = false
                        )
                )
            },
            labelText = stringResource(R.string.recipe_prepTimeMinutes),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isNumeric = true,
            isError = recipeDao.validatePrepTimeMinutes && !recipeDao.isPrepTimeMinutesValid(),
            supportingText = stringResource(R.string.formError_integerField)
        )

        TextInput(
            value = recipeDao.cookTimeMinutes,
            onValueChange = {
                onValueChange(
                    recipeDao
                        .copy(
                            cookTimeMinutes = it,
                            validateCookTimeMinutes = false
                        )
                )
            },
            labelText = stringResource(R.string.recipe_cookTimeMinutes),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            isNumeric = true,
            isError = recipeDao.validateCookTimeMinutes && !recipeDao.isCookTimeMinutesValid(),
            supportingText = stringResource(R.string.formError_integerField)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.recipe_isFavorite))
            Checkbox(
                checked = recipeDao.isFavorite,
                onCheckedChange = {
                    onValueChange(
                        recipeDao
                            .copy(
                                isFavorite = it,
                                validateIsFavorite = false
                            )
                    )
                }
            )
        }

        RecipeTagsInput(
            recipeDao = recipeDao,
            onValueChange = onValueChange,
            enabled = enabled,
            openTagListPopup = openTagListPopup,

            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen,
            filterName = tagListFilterName,
            updateFilterName = tagListUpdateFilterName
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
            onTakeImage = {},
            onPickImage = {},
            onClearImage = {},
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {},
            recipeImage = RecipeExamples.recipeImageBitmap,
            tagListUpdateFilterName = {}
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
            onTakeImage = {},
            onPickImage = {},
            onClearImage = {},
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {},
            isTagListPopupOpen = true,
            recipeImage = RecipeExamples.recipeImageBitmap,
            tagListUpdateFilterName = {}
        )
    }
}

@DefaultPreview
@Composable
private fun RecipeFormBodyScreenErrorPreview() {
    RecipeBookTheme {
        RecipeFormBody(
            recipeUiState = RecipeUiState(
                RecipeExamples.recipe1.toRecipeDao()
                    .copy(
                        name = "",
                        servingsNum = "a",
                        prepTimeMinutes = "a",

                        cookTimeMinutes = "a",
                        isFavorite = false,

                        validateName = true,
                        validateServingsNum = true,
                        validatePrepTimeMinutes = true,
                        validateCookTimeMinutes = true,
                        validateIsFavorite = true
                    )
                    .apply {
                        methodList.forEach {
                            it.value = ""

                            it.validateValue = true
                        }

                        ingredientItemList.forEach {
                            when (it) {
                                is IngredientDao -> {
                                    it.name = ""
                                    it.quantity = "a"
                                    it.value = ""

                                    it.validateName = true
                                    it.validateQuantity = true
                                    it.validateValue = true
                                }

                                is IngredientGroupTitleDao -> {
                                    it.title = null

                                    it.validateTitle = true
                                }
                            }
                        }
                    }
            ),
            onRecipeValueChange = {},
            onSaveClick = {},
            onTakeImage = {},
            onPickImage = {},
            onClearImage = {},
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {},
            recipeImage = RecipeExamples.recipeImageBitmap,
            tagListUpdateFilterName = {}
        )
    }
}

//endregion