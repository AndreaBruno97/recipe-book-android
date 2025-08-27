@file:OptIn(ExperimentalLayoutApi::class)

package com.example.recipebook.ui.composables.common.tagListSelector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.composables.common.utility.CardDialog
import com.example.recipebook.ui.composables.common.utility.ClearableItem
import com.example.recipebook.ui.composables.common.utility.TagChip
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun TagListSelectorBody(
    modifier: Modifier = Modifier,
    unusedTagList: List<Tag>,
    selectedTagList: List<Tag>,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    filterName: String = "",
    enabled: Boolean,
    updateFilterName: (String) -> Unit,
    onTagSelect: (Tag) -> Unit,
    onTagRemoval: (Tag) -> Unit
) {
    CardDialog(
        title = stringResource(R.string.tagListSelector_popupTitle),
        isOpen = isTagListPopupOpen,
        closeDialog = closeTagListPopup,
        modifier = modifier
    ) {
        TagListSelectorPopupContent(
            tagList = unusedTagList,
            selectedTagList = selectedTagList,
            enabled = enabled,
            onTagSelect = onTagSelect,
            onTagRemoval = onTagRemoval,
            filterName = filterName,
            updateFilterName = updateFilterName
        )
    }
}

@Composable
private fun TagListSelectorPopupContent(
    tagList: List<Tag>,
    selectedTagList: List<Tag>,
    enabled: Boolean,
    onTagSelect: (Tag) -> Unit,
    onTagRemoval: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    filterName: String = "",
    updateFilterName: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        ClearableItem(
            modifier = Modifier
                .fillMaxWidth(),
            clearItem = { updateFilterName("") }
        ) { clearableItemModifier ->
            TextInput(
                value = filterName,
                onValueChange = updateFilterName,
                enabled = enabled,
                modifier = clearableItemModifier
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7F)
        ) {
            items(tagList) { tag ->
                TagChip(
                    tag = tag.toTagDao(),
                    enabled = enabled,
                    onClick = {
                        onTagSelect(tag)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.padding_medium))
        )

        Text(stringResource(R.string.tagListSelector_selectedSection))

        FlowRow(
            overflow = FlowRowOverflow.Clip,
            horizontalArrangement = Arrangement
                .spacedBy(dimensionResource(R.dimen.padding_medium)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3F)
                .verticalScroll(rememberScrollState())
        ) {
            for (tag in selectedTagList) {
                TagChip(
                    tag = tag.toTagDao(),
                    showCloseIcon = true,
                    onClick = { onTagRemoval(tag) }
                )
            }
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun TagListPopupContentPreview() {
    RecipeBookTheme {
        TagListSelectorPopupContent(
            tagList = TagExamples.tagList,
            selectedTagList = listOf(
                TagExamples.tag1, TagExamples.tag2, TagExamples.tag1, TagExamples.tag2,
                TagExamples.tag1, TagExamples.tag2, TagExamples.tag1, TagExamples.tag2,
                TagExamples.tag1, TagExamples.tag2, TagExamples.tag1, TagExamples.tag2
            ),
            enabled = true,
            onTagSelect = {},
            onTagRemoval = {},
            updateFilterName = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagListSelectorPreview() {
    RecipeBookTheme {
        TagListSelectorBody(
            unusedTagList = TagExamples.tagList,
            selectedTagList = listOf(
                TagExamples.tag1, TagExamples.tag2, TagExamples.tag1, TagExamples.tag2,
                TagExamples.tag1, TagExamples.tag2, TagExamples.tag1, TagExamples.tag2,
                TagExamples.tag1, TagExamples.tag2, TagExamples.tag1, TagExamples.tag2
            ),
            isTagListPopupOpen = true,
            enabled = true,
            onTagSelect = {},
            onTagRemoval = {},
            updateFilterName = {},
            closeTagListPopup = {}
        )
    }
}

//endregion