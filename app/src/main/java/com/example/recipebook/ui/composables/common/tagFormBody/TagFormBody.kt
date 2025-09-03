@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.common.tagFormBody

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
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
import com.example.recipebook.ui.composables.common.utility.ClearableItem
import com.example.recipebook.ui.composables.common.utility.TagChip
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.composables.common.utility.WarningConfirmationDialog
import com.example.recipebook.ui.composables.common.utility.WarningDialogType
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.tagForm_selectedEmojiTextStyle

@Composable
fun TagFormBody(
    tagUiState: TagUiState,
    validateName: Boolean,
    modifier: Modifier = Modifier,
    onTagValueChange: (TagDao) -> Unit,
    onSaveClick: () -> Unit,
    isNamePresent: () -> Boolean,
    isDeletePopupOpen: Boolean = false,
    openTagDeletePopup: () -> Unit,
    closeTagDeletePopup: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isNamePresentFlag = isNamePresent()
    val isUpdateForm = tagUiState.tagDao._id != null

    Column(
        verticalArrangement = Arrangement
            .spacedBy(dimensionResource(id = R.dimen.padding_medium)),
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TagChip(tagUiState.tagDao)
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            TagInputForm(
                tagDao = tagUiState.tagDao,
                validateName = validateName,
                onValueChange = onTagValueChange,
                modifier = Modifier.fillMaxWidth(),
                isNamePresent = isNamePresentFlag
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = openTagDeletePopup,
                modifier = Modifier
                    .weight(0.45F),
                enabled = isUpdateForm
            ) {
                Text(text = stringResource(R.string.tagList_body_delete))
            }

            Spacer(modifier = Modifier.weight(0.1F))

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .weight(0.45F)
            ) {
                Text(text = stringResource(R.string.save_button_label))
            }
        }
    }

    WarningConfirmationDialog(
        isPopupOpen = isDeletePopupOpen,
        text = stringResource(R.string.tagForm_deletePopupText),
        warningType = WarningDialogType.DELETE,
        onWarningConfirm = {
            closeTagDeletePopup()
            onDeleteClick()
        },
        onWarningCancel = closeTagDeletePopup,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    )

}

@Composable
fun TagInputForm(
    tagDao: TagDao,
    validateName: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: (TagDao) -> Unit = {},
    isNamePresent: Boolean,
    enabled: Boolean = true
) {
    val currentIconText = tagDao.icon ?: stringResource(R.string.tag_icon_empty)
    val showRepeatedNameError = isNamePresent
    val showEmptyNameError = validateName && tagDao.name.isBlank()
    val supportingText = if (showRepeatedNameError) {
        stringResource(R.string.tag_nameAlreadyPresent)
    } else {
        null
    }
    val colorsPerRow = 5

    Column(modifier = modifier) {
        TextInput(
            value = tagDao.name,
            onValueChange = { onValueChange(tagDao.copy(name = it)) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            labelText = stringResource(R.string.tag_name),
            isError = showEmptyNameError || showRepeatedNameError,
            supportingText = supportingText
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            maxItemsInEachRow = colorsPerRow
        ) {
            for (tagColor in tagColorList) {
                Row(
                    modifier = Modifier.weight(1F),
                    horizontalArrangement = Arrangement.Center
                ) {
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

            /*
                Extra elements to complete the row and create a grid
                Without the elements, the last row would have less
                elements, but centered

                Without extra elements
                |  *  |  *  |  *  |  *  |  *  |
                |  *  |  *  |  *  |  *  |  *  |
                |      *       |      *       |

                With extra elements
                |  *  |  *  |  *  |  *  |  *  |
                |  *  |  *  |  *  |  *  |  *  |
                |  *  |  *  |     |     |     |
             */

            val emptySpacesInLastRow = tagColorList.size.mod(colorsPerRow)

            repeat(emptySpacesInLastRow) {
                Row(
                    modifier = Modifier.weight(1F)
                ) {}
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.tagForm_iconTitle))

            Spacer(
                modifier = Modifier.width(dimensionResource(R.dimen.padding_large))
            )

            ClearableItem(
                clearItem = { onValueChange(tagDao.copy(icon = null)) },
                enabled = enabled && tagDao.icon != null
            ) { clearableItemModifier ->
                Text(
                    text = currentIconText,
                    style = tagForm_selectedEmojiTextStyle
                )
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
}

//region Preview

@DefaultPreview
@Composable
private fun TagFormBodyScreenPreview() {
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(
                TagExamples.tag1.toTagDao()
            ),
            validateName = false,
            onTagValueChange = {},
            onSaveClick = {},
            isNamePresent = { false },
            onDeleteClick = {},
            openTagDeletePopup = {},
            closeTagDeletePopup = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagFormBodyRepeatedNameScreenPreview() {
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(
                TagExamples.tag1.toTagDao()
            ),
            validateName = false,
            onTagValueChange = {},
            onSaveClick = {},
            isNamePresent = { true },
            onDeleteClick = {},
            openTagDeletePopup = {},
            closeTagDeletePopup = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagFormBodyEmptyNameScreenPreview() {
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(
                TagExamples.tag1.toTagDao()
                    .copy(name = "")
            ),
            validateName = true,
            onTagValueChange = {},
            onSaveClick = {},
            isNamePresent = { false },
            onDeleteClick = {},
            openTagDeletePopup = {},
            closeTagDeletePopup = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagFormBodyEmptyIconScreenPreview() {
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(
                TagExamples.tag1.toTagDao()
                    .copy(icon = null)
            ),
            validateName = true,
            onTagValueChange = {},
            onSaveClick = {},
            isNamePresent = { false },
            onDeleteClick = {},
            openTagDeletePopup = {},
            closeTagDeletePopup = {}
        )
    }
}

@DefaultPreview
@Composable
private fun TagFormBodyCreateScreenPreview() {
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(),
            validateName = true,
            onTagValueChange = {},
            onSaveClick = {},
            isNamePresent = { false },
            onDeleteClick = {},
            openTagDeletePopup = {},
            closeTagDeletePopup = {}
        )
    }
}

//endregion