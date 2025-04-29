package com.example.recipebook.ui.composables.tagList.internal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.TagForm_DeleteTag
import org.mongodb.kbson.ObjectId


@Composable
fun TagListBody(
    tagList: List<Tag>,
    screenSize: ScreenSize,
    openPopup: (ObjectId?) -> Unit,
    onDelete: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding))
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (tagList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_tags_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            TagList(
                tagList = tagList,
                openPopup = openPopup,
                onDelete = onDelete,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun TagList(
    tagList: List<Tag>,
    openPopup: (ObjectId?) -> Unit,
    onDelete: (Tag) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(tagList) { tag ->
            TagRow(
                tag = tag,
                onDelete = onDelete,
                modifier = Modifier
                    .clickable {
                        openPopup(tag._id)
                    }
            )
        }
    }
}

@Composable
private fun TagRow(
    tag: Tag,
    modifier: Modifier = Modifier,
    onDelete: (Tag) -> Unit
) {
    var rowText = tag.name
    if (tag.icon != null) {
        rowText = "${tag.icon} $rowText"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rowText,
            modifier = modifier,
            color = tag.colorObj
        )
        IconButton(
            onClick = { onDelete(tag) }
        ) {
            Icon(imageVector = TagForm_DeleteTag, contentDescription = "")
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
        openPopup = {},
        onDelete = {}
    )
}

//endregion