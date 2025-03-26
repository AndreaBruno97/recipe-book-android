package com.example.recipebook.ui.composables.commonComposable.TagFormBody

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun TagFormBody(
    tagUiState: TagUiState,
    onTagValueChange: (TagDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ){
        TagInputForm(
            tagDetails = tagUiState.tagDetails,
            onValueChange = onTagValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = tagUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_button_label))
        }
    }
}

@Composable
fun TagInputForm(
    tagDetails: TagDetails,
    modifier: Modifier = Modifier,
    onValueChange: (TagDetails) -> Unit = {},
    enabled: Boolean = true
){
    OutlinedTextField(
        value = tagDetails.name,
        onValueChange = { onValueChange(tagDetails.copy(name = it)) },
        label = {Text(stringResource(R.string.tag_name))},
        enabled = enabled,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun TagFormBodyScreenPreview(){
    RecipeBookTheme {
        TagFormBody(
            tagUiState = TagUiState(
                TagExamples.tag1.toTagDetails()
            ),
            onTagValueChange = {},
            onSaveClick = {}
        )
    }
}