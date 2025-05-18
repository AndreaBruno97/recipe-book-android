@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.recipeEdit


import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeFormBody
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorViewModel
import com.example.recipebook.ui.composables.common.utility.createCameraLauncherState
import com.example.recipebook.ui.navigation.NavigationDestinationRecipeId
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RecipeEditDestination : NavigationDestinationRecipeId {
    override val route = "recipe_edit"
    override val titleRes = R.string.routeTitle_recipeEdit
    const val recipeIdArg = "recipeId"
    val routeWithArgs = "$route/{$recipeIdArg}"
}

@Composable
fun RecipeEditScreen(
    screenSize: ScreenSize,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    modifier: Modifier = Modifier,
    recipeViewModel: RecipeEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tagListSelectorViewModel: TagListSelectorViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val currentContext = LocalContext.current

    val cameraLauncherState = createCameraLauncherState(currentContext, recipeViewModel)
    recipeViewModel.loadRecipeImage(currentContext)

    val recipeUiState = recipeViewModel.recipeUiState
    val tagListUiState by tagListSelectorViewModel.tagListUiState.collectAsState()
    val tagListFilterState by tagListSelectorViewModel.tagListFilterState.collectAsState()
    val usedTagIdList = recipeUiState.recipeDao.tagList.map { it._id }
    val unusedTagList = tagListUiState.tagDetailList.filter { it._id !in usedTagIdList }

    RecipeEditScreenStateCollector(
        screenSize = screenSize,
        navigateBack = navigateBack,
        onNavigateUp = onNavigateUp,
        navigateToRecipeDetails = { navigateToRecipeDetails(recipeViewModel.recipeId) },
        modifier = modifier,
        recipeUiState = recipeViewModel.recipeUiState,
        onRecipeValueChange = recipeViewModel::updateUiState,
        updateRecipe = { recipeViewModel.updateRecipe(currentContext) },
        unusedTagList = unusedTagList,
        openTagListPopup = tagListSelectorViewModel::openTagListPopup,
        closeTagListPopup = tagListSelectorViewModel::closeTagListPopup,
        isTagListPopupOpen = tagListSelectorViewModel.isTagListPopupOpen,
        takeImage = cameraLauncherState::takeImage,
        pickImage = cameraLauncherState::pickImage,
        clearImage = recipeViewModel::clearImage,
        recipeImage = recipeViewModel.tempImage,
        tagListFilterName = tagListFilterState.filterName,
        tagListUpdateFilterName = tagListSelectorViewModel::updateFilterName
    )
}

@Composable
fun RecipeEditScreenStateCollector(
    screenSize: ScreenSize,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToRecipeDetails: () -> Unit,
    modifier: Modifier = Modifier,
    recipeUiState: RecipeUiState,
    onRecipeValueChange: (RecipeDao) -> Unit,
    updateRecipe: suspend () -> Boolean,
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
                title = stringResource(RecipeEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        RecipeFormBody(
            recipeUiState = recipeUiState,
            onRecipeValueChange = onRecipeValueChange,
            onSaveClick = {
                coroutineScope.launch {
                    val isUpdated = updateRecipe()
                    if (isUpdated) {
                        navigateToRecipeDetails()
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
                .verticalScroll(rememberScrollState()),
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
fun RecipeEditScreenPhonePreview() {
    RecipeBookTheme {
        RecipeEditScreenStateCollector(
            ScreenSize.SMALL,
            navigateBack = {},
            onNavigateUp = {},
            navigateToRecipeDetails = {},
            recipeUiState = RecipeUiState(RecipeExamples.recipe1.toRecipeDao()),
            onRecipeValueChange = {},
            updateRecipe = { false },
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
fun RecipeEditScreenTabletPreview() {
    RecipeBookTheme {
        RecipeEditScreenStateCollector(
            ScreenSize.LARGE,
            navigateBack = {},
            onNavigateUp = {},
            navigateToRecipeDetails = {},
            recipeUiState = RecipeUiState(RecipeExamples.recipe1.toRecipeDao()),
            onRecipeValueChange = {},
            updateRecipe = { false },
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