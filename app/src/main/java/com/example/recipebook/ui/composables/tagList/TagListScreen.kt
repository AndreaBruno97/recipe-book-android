@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.tagList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.common.tagFormBody.TagFormBody
import com.example.recipebook.ui.composables.common.tagFormBody.TagFormBodyViewModel
import com.example.recipebook.ui.composables.common.tagFormBody.TagUiState
import com.example.recipebook.ui.composables.common.utility.CardDialog
import com.example.recipebook.ui.composables.common.utility.ClearableItem
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.composables.tagList.internal.TagListBody
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.TagList_FabAddTag
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object TagListDestination : NavigationDestinationNoParams {
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
) {
    val coroutineScope = rememberCoroutineScope()

    val tagListUiState by tagListViewModel.tagListUiState.collectAsState()
    val filterState by tagListViewModel.filterState.collectAsState()

    TagListStateCollector(
        screenSize = screenSize,
        modifier = modifier,
        navigateBack = navigateBack,
        tagList = tagListUiState.tagList,
        isPopupOpen = tagListViewModel.isPopupOpen,
        validateName = tagViewModel.validateName,
        currentTagId = tagListViewModel.currentTagId,
        filterName = filterState.filterName,
        enabled = true,
        openPopup = {
            tagViewModel.loadTag(it)
            tagListViewModel.openPopup(it)
        },
        closePopup = tagListViewModel::closePopup,
        tagUiState = tagViewModel.tagUiState,
        onTagValueChange = tagViewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                val isTagSaved = tagViewModel.updateTag()
                if (isTagSaved) {
                    tagListViewModel.closePopup()
                }
            }
        },
        onDelete = {
            coroutineScope.launch {
                tagListViewModel.deleteTag(it)
            }
        },
        isNamePresent = tagViewModel::isNamePresent,
        updateFilterName = tagListViewModel::updateFilterName
    )
}

@Composable
fun TagListStateCollector(
    screenSize: ScreenSize,
    navigateBack: () -> Unit,
    tagList: List<Tag>,
    modifier: Modifier = Modifier,
    isPopupOpen: Boolean = false,
    validateName: Boolean = false,
    currentTagId: ObjectId? = null,
    filterName: String = "",
    enabled: Boolean = true,
    openPopup: (ObjectId?) -> Unit,
    closePopup: () -> Unit,
    tagUiState: TagUiState,
    onTagValueChange: (TagDao) -> Unit,
    onSaveClick: () -> Unit,
    onDelete: (Tag) -> Unit,
    isNamePresent: () -> Boolean,
    updateFilterName: (String) -> Unit
) {
    val scrollBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
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
            ) {
                Icon(
                    imageVector = TagList_FabAddTag,
                    contentDescription = stringResource(R.string.edit_tag_button_text)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ClearableItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                clearItem = { updateFilterName("") }
            ) { clearableItemModifier ->
                TextInput(
                    value = filterName,
                    onValueChange = updateFilterName,
                    enabled = enabled,
                    modifier = clearableItemModifier
                )
            }
            TagListBody(
                tagList = tagList,
                screenSize = screenSize,
                modifier = modifier.fillMaxSize(),
                //contentPadding = innerPadding,
                openPopup = openPopup,
                onDelete = onDelete
            )
        }

        CardDialog(
            isOpen = isPopupOpen,
            closeDialog = closePopup
        ) {
            TagFormBody(
                tagUiState = tagUiState,
                validateName = validateName,
                onTagValueChange = onTagValueChange,
                onSaveClick = onSaveClick,
                isNamePresent = isNamePresent
            )
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun TagListScreenPhonePreview() {
    RecipeBookTheme {
        TagListStateCollector(
            ScreenSize.SMALL,
            navigateBack = {},
            TagExamples.tagList,
            openPopup = {},
            closePopup = {},
            tagUiState = TagUiState(TagExamples.tag1.toTagDao()),
            onTagValueChange = {},
            onSaveClick = {},
            onDelete = {},
            isNamePresent = { false },
            updateFilterName = {}
        )
    }
}

@PhonePreview
@Composable
fun TagListScreenPhonePopupPreview() {
    RecipeBookTheme {
        TagListStateCollector(
            ScreenSize.SMALL,
            navigateBack = {},
            TagExamples.tagList,
            isPopupOpen = true,
            openPopup = {},
            closePopup = {},
            tagUiState = TagUiState(TagExamples.tag1.toTagDao()),
            onTagValueChange = {},
            onSaveClick = {},
            onDelete = {},
            isNamePresent = { false },
            updateFilterName = {}
        )
    }
}

//endregion