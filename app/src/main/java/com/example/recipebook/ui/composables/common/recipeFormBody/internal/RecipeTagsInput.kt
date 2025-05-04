package com.example.recipebook.ui.composables.common.recipeFormBody.internal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.composables.common.utility.SortableList
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeForm_AddTag

@Composable
fun RecipeTagsInput(
    recipeDao: RecipeDao,
    onValueChange: (RecipeDao) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    openTagListPopup: () -> Unit,
    unusedTagList: List<Tag>,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false
) {
    Column(
        modifier = modifier
    ) {
        Text(stringResource(R.string.recipe_tags))

        SortableList<TagDao>(
            itemList = recipeDao.tagList,
            updateList = { tagList ->
                onValueChange(recipeDao
                    .copy(tagList = tagList)
                    .apply { validateTagList = false }
                )
            },
            onClickNewItem = openTagListPopup,
            newItemButtonIcon = RecipeForm_AddTag,
            newItemButtonText = R.string.recipeForm_addTag,
            enabled = enabled
        ) { tag, index, modifier ->
            TagInputLine(
                tag = tag,
                modifier = modifier,
                enabled = enabled
            )
        }
    }

    if (isTagListPopupOpen) {
        Dialog(onDismissRequest = closeTagListPopup) {
            Card(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)),
                shape = RoundedCornerShape(16.dp)
            ) {
                TagListPopupContent(
                    tagList = unusedTagList,
                    enabled = enabled,
                    onTagSelect = {
                        onValueChange(
                            recipeDao.copy(
                                tagList = recipeDao.tagList
                                    .plus(it.toTagDao())
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TagInputLine(
    tag: TagDao,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    val tagName = if (tag.icon != null) {
        "${tag.icon} ${tag.name}"
    } else {
        tag.name
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = tagName,
            color = tag.color
        )
    }
}

@Composable
private fun TagListPopupContent(
    tagList: List<Tag>,
    enabled: Boolean,
    onTagSelect: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(tagList) { tag ->
            TagInputLine(
                tag = tag.toTagDao(),
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .clickable {
                        onTagSelect(tag)
                    }
            )
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun RecipeTagsInputPreview() {
    RecipeBookTheme {
        RecipeTagsInput(
            recipeDao = RecipeExamples.recipe1.toRecipeDao(),
            onValueChange = {},
            enabled = true,
            openTagListPopup = {},
            unusedTagList = TagExamples.tagList,
            closeTagListPopup = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagInputLinePreview() {
    RecipeBookTheme {
        TagInputLine(
            tag = TagExamples.tag1.toTagDao(),
            enabled = true
        )
    }
}

@DefaultPreview
@Composable
private fun TagListPopupContentPreview() {
    RecipeBookTheme {
        TagListPopupContent(
            tagList = TagExamples.tagList,
            enabled = true,
            onTagSelect = {}
        )
    }
}

//endregion