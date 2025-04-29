package com.example.recipebook.ui.composables.common.tagFormBody

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.tagColorList
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.TagForm_DeleteIcon

@Composable
fun TagFormBody(
    tagUiState: TagUiState,
    onTagValueChange: (TagDao) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        TagInputForm(
            tagDao = tagUiState.tagDao,
            onValueChange = onTagValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSaveClick,
            enabled = tagUiState.tagDao.validateInput(),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_button_label))
        }
    }
}

@Composable
fun TagInputForm(
    tagDao: TagDao,
    modifier: Modifier = Modifier,
    onValueChange: (TagDao) -> Unit = {},
    enabled: Boolean = true
) {
    val currentIconText = tagDao.icon ?: stringResource(R.string.tag_icon_empty)

    OutlinedTextField(
        value = tagDao.name,
        onValueChange = { onValueChange(tagDao.copy(name = it)) },
        label = { Text(stringResource(R.string.tag_name)) },
        enabled = enabled,
        modifier = modifier
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tagColorList) { tagColor ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.tag_color_external_size))
                    .border(
                        width = dimensionResource(id = R.dimen.tag_color_border_size),
                        color = if (tagDao.color == tagColor) {
                            tagColor
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape,
                    )
                    .clickable {
                        if (tagDao.color != tagColor) {
                            onValueChange(tagDao.copy(color = tagColor))
                        }
                    }

            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.tag_color_internal_size))
                        .background(tagColor, CircleShape)
                ) {}
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.tag_icon, currentIconText))
        IconButton(
            onClick = { onValueChange(tagDao.copy(icon = null)) },
            enabled = enabled && tagDao.icon != null
        ) {
            Icon(imageVector = TagForm_DeleteIcon, contentDescription = "")
        }
    }

    AndroidView(
        factory = { context ->
            EmojiPickerView(context).apply {
                emojiGridColumns = 10
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { view ->
            view.setOnEmojiPickedListener {
                onValueChange(tagDao.copy(icon = it.emoji))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.tag_emoji_picker_height))
    )
}

@DefaultPreview
@Composable
private fun TagFormBodyScreenPreview() {
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(
                TagExamples.tag1.toTagDao()
            ),
            onTagValueChange = {},
            onSaveClick = {}
        )
    }
}