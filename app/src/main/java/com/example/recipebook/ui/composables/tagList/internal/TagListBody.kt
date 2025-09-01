package com.example.recipebook.ui.composables.tagList.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.composables.common.utility.EmptyListText
import com.example.recipebook.ui.composables.common.utility.TagChip
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.TagList_EmptyList
import org.mongodb.kbson.ObjectId


@Composable
fun TagListBody(
    tagList: List<Tag>,
    screenSize: ScreenSize,
    openPopup: (ObjectId?) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnNum = when (screenSize) {
        ScreenSize.SMALL -> 1
        ScreenSize.MEDIUM -> 2
        ScreenSize.LARGE -> 3
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (tagList.isEmpty()) {
            EmptyListText(
                text = stringResource(R.string.no_tags_description),
                icon = TagList_EmptyList
            )
        } else {
            TagList(
                tagList = tagList,
                columnNum = columnNum,
                openPopup = openPopup
            )
        }
    }
}

@Composable
private fun TagList(
    tagList: List<Tag>,
    columnNum: Int,
    openPopup: (ObjectId?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnNum),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
    ) {
        items(tagList) { tag ->
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                TagChip(
                    tag = tag.toTagDao(),
                    onClick = { openPopup(tag._id) }
                )
            }
        }
    }
}

//region Preview

@DefaultPreview
@Composable
fun TagListPreview() {
    TagListBody(
        tagList = TagExamples.tagList,
        screenSize = ScreenSize.SMALL,
        openPopup = {}
    )
}

@DefaultPreview
@Composable
fun TagListMediumPreview() {
    TagListBody(
        tagList = TagExamples.tagList,
        screenSize = ScreenSize.MEDIUM,
        openPopup = {}
    )
}

@DefaultPreview
@Composable
fun TagListEmptyPreview() {
    TagListBody(
        tagList = listOf(),
        screenSize = ScreenSize.SMALL,
        openPopup = {}
    )
}

//endregion