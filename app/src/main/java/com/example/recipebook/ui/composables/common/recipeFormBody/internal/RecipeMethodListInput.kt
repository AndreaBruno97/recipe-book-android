package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.method.MethodDao
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.ui.composables.common.utility.SortableList
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeForm_AddMethod

@Composable
fun RecipeMethodListInput(
    recipeDao: RecipeDao,
    onValueChange: (RecipeDao) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val newMethodList = recipeDao.methodList.map { it.copy() }

    Column(
        modifier = modifier
    ) {
        Text(stringResource(R.string.recipe_method) + "*")

        SortableList<MethodDao>(
            itemList = recipeDao.methodList,
            updateList = { methodList ->
                onValueChange(
                    recipeDao
                        .copy(
                            methodList = methodList,
                            validateMethodList = false
                        )
                )
            },
            onClickNewItem = {
                onValueChange(
                    recipeDao.copy(
                        methodList =
                        recipeDao.methodList.plus(listOf(MethodDao()))
                    )
                )
            },
            newItemButtonIcon = RecipeForm_AddMethod,
            newItemButtonText = R.string.recipeForm_addMethod,
            enabled = enabled,
            showEmptyError = recipeDao.validateMethodList
        ) { method, index, modifier ->
            TextInput(
                value = method.value,
                onValueChange = { newValue ->
                    val methodToUpdate = newMethodList[index]

                    methodToUpdate.value = newValue
                    methodToUpdate.validateValue = false

                    onValueChange(recipeDao.copy(methodList = newMethodList))
                },
                enabled = enabled,
                modifier = modifier,
                singleLine = false,
                isError = method.validateValue && !method.isValueValid(),
                supportingText = stringResource(R.string.formError_mandatoryField)
            )
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeIngredientsInputPreview() {
    RecipeBookTheme {
        RecipeMethodListInput(
            recipeDao = RecipeExamples.recipe1.toRecipeDao(),
            onValueChange = {},
            enabled = true,
        )
    }
}


@DefaultPreview
@Composable
private fun RecipeIngredientsInputErrorPreview() {
    RecipeBookTheme {
        RecipeMethodListInput(
            recipeDao = RecipeExamples.recipe1.toRecipeDao()
                .copy()
                .apply {
                    methodList.forEach {
                        it.value = ""
                        it.validateValue = true
                    }
                },
            onValueChange = {},
            enabled = true,
        )
    }
}

//endregion