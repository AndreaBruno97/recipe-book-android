
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.tagList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.TagDetails
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.TagFormBody
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.TagFormBodyViewModel
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.TagUiState
import com.example.recipebook.ui.composables.commonComposable.TagFormBody.toTagDetails
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeForm_DeleteIngredient
import com.example.recipebook.ui.theme.TagForm_DeleteTag
import com.example.recipebook.ui.theme.TagList_FabAddTag
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object TagListDestination: NavigationDestinationNoParams {
    override val route = "tagList"
    override val titleRes = R.string.routeTitle_tagList
}

@Composable
fun TagListScreen(
    screenSize: ScreenSize,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    tagListViewModel: TagListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tagViewModel: TagFormBodyViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()

    val tagListUiState by tagListViewModel.tagListUiState.collectAsState()

    TagListStateCollector(
        screenSize = screenSize,
        modifier = modifier,
        navigateBack = navigateBack,
        tagList = tagListUiState.tagList,
        isPopupOpen = tagListViewModel.isPopupOpen,
        currentTagId = tagListViewModel.currentTagId,
        openPopup = {
            tagViewModel.loadTag(it)
            tagListViewModel.openPopup(it)
        },
        closePopup = tagListViewModel::closePopup,
        tagUiState = tagViewModel.tagUiState,
        onTagValueChange = tagViewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch{
                tagViewModel.updateTag()
                tagListViewModel.closePopup()
            }
        },
        onDelete = {
            coroutineScope.launch {
                tagListViewModel.deleteTag(it)
            }
        }
    )
}

@Composable
fun TagListStateCollector(
    screenSize: ScreenSize,
    navigateBack: () -> Unit,
    tagList: List<Tag>,
    modifier: Modifier = Modifier,
    isPopupOpen: Boolean = false,
    currentTagId: ObjectId? = null,
    openPopup: (ObjectId?) -> Unit,
    closePopup: () -> Unit,
    tagUiState: TagUiState,
    onTagValueChange: (TagDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDelete: (Tag) -> Unit
){
    val scrollBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = modifier.nestedScroll(scrollBarBehavior.nestedScrollConnection),
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(TagListDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBarBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openPopup(null) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ){
                Icon(
                    imageVector = TagList_FabAddTag,
                    contentDescription = stringResource(R.string.edit_tag_button_text)
                )
            }
        }
    ){ innerPadding ->
        TagListBody(
            tagList = tagList,
            screenSize = screenSize,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            openPopup = openPopup,
            onDelete = onDelete
        )
        if(isPopupOpen){
            Dialog(onDismissRequest = closePopup) {
                Card(
                    modifier = Modifier
                        /*
                        .height(100.dp)
                        .width(100.dp)
                        */
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    shape = RoundedCornerShape(16.dp)
                ){
                    TagFormBody(
                        tagUiState = tagUiState,
                        onTagValueChange = onTagValueChange,
                        onSaveClick = onSaveClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TagListBody(
    tagList: List<Tag>,
    screenSize: ScreenSize,
    openPopup: (ObjectId?) -> Unit,
    onDelete: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding))
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        if(tagList.isEmpty()){
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
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(tagList){ tag ->
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
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = tag.name,
            modifier = modifier

        )
        IconButton(
            onClick = { onDelete(tag) }
        ) {
            Icon(imageVector = TagForm_DeleteTag, contentDescription = "")
        }
    }
}

@PhonePreview
@Composable
fun TagListScreenPhonePreview(){
    RecipeBookTheme {
        TagListStateCollector(
            ScreenSize.SMALL,
            navigateBack = {},
            TagExamples.tagList,
            openPopup = {},
            closePopup = {},
            tagUiState = TagUiState(TagExamples.tag1.toTagDetails()),
            onTagValueChange = {},
            onSaveClick = {},
            onDelete = {}
        )
    }
}

@PhonePreview
@Composable
fun TagListScreenPhonePopupPreview(){
    RecipeBookTheme {
        TagListStateCollector(
            ScreenSize.SMALL,
            navigateBack = {},
            TagExamples.tagList,
            isPopupOpen = true,
            openPopup = {},
            closePopup = {},
            tagUiState = TagUiState(TagExamples.tag1.toTagDetails()),
            onTagValueChange = {},
            onSaveClick = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagListPreview(){
    TagListBody(
        tagList = TagExamples.tagList,
        screenSize = ScreenSize.SMALL,
        openPopup = {},
        onDelete = {}
    )
}