package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.ui.composables.common.utility.SortableList
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
    Column(
        modifier = modifier
    ) {
        Text(stringResource(R.string.recipe_method) + "*")

        SortableList<String>(
            itemList = recipeDao.methodList,
            updateList = { methodList ->
                onValueChange(recipeDao.copy(methodList = methodList))
            },
            onClickNewItem = {
                onValueChange(
                    recipeDao.copy(
                        methodList =
                        recipeDao.methodList.plus(listOf(""))
                    )
                )
            },
            newItemButtonIcon = RecipeForm_AddMethod,
            newItemButtonText = R.string.recipeForm_addMethod,
            enabled = enabled
        ) { method, index, modifier ->
            RecipeTextInput(
                value = method,
                onValueChange = { newMethod ->
                    val newMethodList = recipeDao.methodList.toMutableList()
                    newMethodList[index] = newMethod
                    onValueChange(recipeDao.copy(methodList = newMethodList))
                },
                enabled = enabled,
                modifier = modifier,
                singleLine = false
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

//endregion