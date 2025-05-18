@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.recipeCreate

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeFormBody
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorViewModel
import com.example.recipebook.ui.composables.common.utility.createCameraLauncherState
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RecipeCreateDestination : NavigationDestinationNoParams {
    override val route = "recipe_create"
    override val titleRes = R.string.routeTitle_recipeCreate
}

@Composable
fun RecipeCreateScreen(
    screenSize: ScreenSize,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    recipeViewModel: RecipeCreateViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tagListViewModel: TagListSelectorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currentContext = LocalContext.current

    val cameraLauncherState = createCameraLauncherState(currentContext, recipeViewModel)

    val recipeUiState = recipeViewModel.recipeUiState
    val tagListUiState by tagListViewModel.tagListUiState.collectAsState()
    val tagListFilterState by tagListViewModel.tagListFilterState.collectAsState()
    val usedTagIdList = recipeUiState.recipeDao.tagList.map { it._id }
    val unusedTagList = tagListUiState.tagDetailList.filter { it._id !in usedTagIdList }

    RecipeCreateScreenStateCollector(
        screenSize = screenSize,
        navigateToRecipeDetails = navigateToRecipeDetails,
        onNavigateUp = onNavigateUp,
        canNavigateBack = canNavigateBack,
        recipeUiState = recipeViewModel.recipeUiState,
        onRecipeValueChange = recipeViewModel::updateUiState,
        saveRecipe = { recipeViewModel.saveRecipe(currentContext) },
        unusedTagList = unusedTagList,
        openTagListPopup = tagListViewModel::openTagListPopup,
        closeTagListPopup = tagListViewModel::closeTagListPopup,
        isTagListPopupOpen = tagListViewModel.isTagListPopupOpen,
        takeImage = cameraLauncherState::takeImage,
        pickImage = cameraLauncherState::pickImage,
        clearImage = recipeViewModel::clearImage,
        recipeImage = recipeViewModel.tempImage,
        tagListFilterName = tagListFilterState.filterName,
        tagListUpdateFilterName = tagListViewModel::updateFilterName
    )
}

@Composable
fun RecipeCreateScreenStateCollector(
    screenSize: ScreenSize,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    recipeUiState: RecipeUiState,
    onRecipeValueChange: (RecipeDao) -> Unit,
    saveRecipe: suspend () -> ObjectId?,
    unusedTagList: List<Tag>,
    openTagListPopup: () -> Unit,
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    takeImage: () -> Unit,
    pickImage: () -> Unit,
    clearImage: () -> Unit,
    recipeImage: ImageBitmap?,
    tagListFilterName: String = "",
    tagListUpdateFilterName: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(RecipeCreateDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        RecipeFormBody(
            recipeUiState = recipeUiState,
            onRecipeValueChange = onRecipeValueChange,
            onSaveClick = {
                coroutineScope.launch {
                    val newRecipeId = saveRecipe()

                    if (newRecipeId != null) {
                        navigateToRecipeDetails(newRecipeId)
                    }
                }
            },
            onTakeImage = takeImage,
            onPickImage = pickImage,
            onClearImage = clearImage,
            unusedTagList = unusedTagList,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            openTagListPopup = openTagListPopup,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen,
            recipeImage = recipeImage,
            tagListFilterName = tagListFilterName,
            tagListUpdateFilterName = tagListUpdateFilterName
        )

    }
}

//region Preview

@PhonePreview
@Composable
fun RecipeCreateScreenPhonePreview() {
    RecipeBookTheme {
        RecipeCreateScreenStateCollector(
            ScreenSize.SMALL,
            navigateToRecipeDetails = {},
            onNavigateUp = {},
            canNavigateBack = true,
            recipeUiState = RecipeUiState(),
            onRecipeValueChange = {},
            saveRecipe = suspend { null },
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {},
            takeImage = {},
            pickImage = {},
            clearImage = {},
            recipeImage = RecipeExamples.recipeImageBitmap,
            tagListUpdateFilterName = {}
        )
    }
}

@TabletPreview
@Composable
fun RecipeCreateScreenTabletPreview() {
    RecipeBookTheme {
        RecipeCreateScreenStateCollector(
            ScreenSize.LARGE,
            navigateToRecipeDetails = {},
            onNavigateUp = {},
            canNavigateBack = true,
            recipeUiState = RecipeUiState(),
            onRecipeValueChange = {},
            saveRecipe = suspend { null },
            unusedTagList = TagExamples.tagList,
            openTagListPopup = {},
            closeTagListPopup = {},
            takeImage = {},
            pickImage = {},
            clearImage = {},
            recipeImage = RecipeExamples.recipeImageBitmap,
            tagListUpdateFilterName = {}
        )
    }
}

//endregion