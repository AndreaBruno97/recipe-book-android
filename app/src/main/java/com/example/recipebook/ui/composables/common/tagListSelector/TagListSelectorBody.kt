package com.example.recipebook.ui.composables.common.tagListSelector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.TagList_ClearFilter

@Composable
fun TagListSelectorBody(
    modifier: Modifier = Modifier,
    unusedTagList: List<Tag>,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    filterName: String = "",
    enabled: Boolean,
    updateFilterName: (String) -> Unit,
    onTagSelect: (Tag) -> Unit
) {
    if (isTagListPopupOpen) {
        Dialog(onDismissRequest = closeTagListPopup) {
            Card(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)),
                shape = RoundedCornerShape(16.dp)
            ) {
                TagListSelectorPopupContent(
                    tagList = unusedTagList,
                    enabled = enabled,
                    onTagSelect = onTagSelect,
                    filterName = filterName,
                    updateFilterName = updateFilterName
                )
            }
        }
    }
}

@Composable
private fun TagListSelectorPopupContent(
    tagList: List<Tag>,
    enabled: Boolean,
    onTagSelect: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    filterName: String = "",
    updateFilterName: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            OutlinedTextField(
                value = filterName,
                onValueChange = updateFilterName,
                modifier = Modifier.weight(1F)
            )
            IconButton(
                onClick = { updateFilterName("") }
            ) {
                Icon(
                    imageVector = TagList_ClearFilter,
                    contentDescription = ""
                )
            }
        }

        LazyColumn(
            modifier = modifier
        ) {
            items(tagList) { tag ->
                TagListSelectorLine(
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
}

@Composable
private fun TagListSelectorLine(
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

//region Preview

@DefaultPreview
@Composable
private fun TagListPopupContentPreview() {
    RecipeBookTheme {
        TagListSelectorPopupContent(
            tagList = TagExamples.tagList,
            enabled = true,
            onTagSelect = {},
            updateFilterName = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagInputLinePreview() {
    RecipeBookTheme {
        TagListSelectorLine(
            tag = TagExamples.tag1.toTagDao(),
            enabled = true
        )
    }
}

//endregion