package com.example.recipebook.ui.composables.common.tagFormBody

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

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
    OutlinedTextField(
        value = tagDao.name,
        onValueChange = { onValueChange(tagDao.copy(name = it)) },
        label = { Text(stringResource(R.string.tag_name)) },
        enabled = enabled,
        modifier = modifier
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