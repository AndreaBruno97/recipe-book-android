@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.recipeCreate

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeFormBody
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorViewModel
import com.example.recipebook.ui.composables.common.utility.ImageManagerViewModel
import com.example.recipebook.ui.composables.common.utility.LoadingOverlay
import com.example.recipebook.ui.composables.common.utility.createCameraLauncherState
import com.example.recipebook.ui.composables.recipeCreate.internal.RecipeFromImageSection
import com.example.recipebook.ui.composables.recipeCreate.internal.RecipeFromWebsiteSection
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
    tagListViewModel: TagListSelectorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    imageManagerViewModel: ImageManagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currentContext = LocalContext.current

    val cameraLauncherState =
        createCameraLauncherState(currentContext, imageManagerViewModel) { imageBitmap, imagePath ->
            when (recipeViewModel.currentImageDestination) {
                ImageDestination.MAIN_RECIPE_IMAGE -> {
                    recipeViewModel.updateUiStateImage(imageBitmap, imagePath)
                }

                ImageDestination.IMAGE_TO_RECIPE_CONVERTER -> {
                    recipeViewModel.updateRecipeFromImageBitmap(imageBitmap, imagePath)
                }

                null -> { /* Do nothing */
                }
            }
        }

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
        takeImage = {
            recipeViewModel.updateCurrentImageDestination(ImageDestination.MAIN_RECIPE_IMAGE)
            cameraLauncherState.takeImage()
        },
        pickImage = {
            recipeViewModel.updateCurrentImageDestination(ImageDestination.MAIN_RECIPE_IMAGE)
            cameraLauncherState.pickImage()
        },
        clearImage = { recipeViewModel.updateUiStateImage(null, null) },
        tagListFilterName = tagListFilterState.filterName,
        tagListUpdateFilterName = tagListViewModel::updateFilterName,
        recipeWebsiteUrl = recipeViewModel.recipeWebsiteUrl,
        validateRecipeWebsiteUrl = recipeViewModel.validateRecipeWebsiteUrl,
        isLoading = recipeViewModel.isLoading,
        isRecipeFromWebsiteSectionVisible = recipeViewModel.isRecipeFromWebsiteSectionVisible,
        updateRecipeWebsiteUrl = { recipeViewModel.updateRecipeWebsiteUrl(it) },
        loadRecipeFromWebsite = { recipeViewModel.loadRecipeFromWebsite(currentContext) },
        showRecipeFromWebsiteSection = recipeViewModel::showRecipeFromWebsiteSection,
        hideRecipeFromWebsiteSection = recipeViewModel::hideRecipeFromWebsiteSection,
        isRecipeFromImagePopupOpen = recipeViewModel.isRecipeFromImagePopupOpen,
        imageRecipeBlockContainer = recipeViewModel.imageRecipeBlockContainer,
        recipeFromImage = recipeViewModel::recipeFromImage,
        updateBlockContainer = recipeViewModel::updateBlockContainer,
        closeRecipeFromImagePopup = recipeViewModel::closeRecipeFromImagePopup,
        loadRecipeFromImageResult = recipeViewModel::loadRecipeFromImageResult,
        recipeFromImageTakeImage = {
            recipeViewModel.resetRecipeFromImage()
            recipeViewModel.updateCurrentImageDestination(ImageDestination.IMAGE_TO_RECIPE_CONVERTER)
            cameraLauncherState.takeImage()
        },
        recipeFromImagePickImage = {
            recipeViewModel.resetRecipeFromImage()
            recipeViewModel.updateCurrentImageDestination(ImageDestination.IMAGE_TO_RECIPE_CONVERTER)
            cameraLauncherState.pickImage()
        },
        recipeFromImageBitmap = recipeViewModel.recipeFromImageBitmap,
        recipeFromImageOrientation = recipeViewModel.recipeFromImageOrientation,
        updateBlockType = recipeViewModel::updateBlockType,
        updateBlockElementIndex = recipeViewModel::updateBlockElementIndex,
        updateBlockIsCollapsed = recipeViewModel::updateBlockIsCollapsed,
        rotateRecipeFromImage90Right = recipeViewModel::rotateRecipeFromImage90Right,
        rotateRecipeFromImage90Left = recipeViewModel::rotateRecipeFromImage90Left
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
    tagListFilterName: String = "",
    tagListUpdateFilterName: (String) -> Unit,
    recipeWebsiteUrl: String = "",
    validateRecipeWebsiteUrl: RecipeWebsiteUrlErrors? = null,
    isLoading: Boolean = false,
    isRecipeFromWebsiteSectionVisible: Boolean = true,
    updateRecipeWebsiteUrl: (String) -> Unit,
    loadRecipeFromWebsite: () -> Unit,
    showRecipeFromWebsiteSection: () -> Unit,
    hideRecipeFromWebsiteSection: () -> Unit,
    isRecipeFromImagePopupOpen: Boolean = false,
    imageRecipeBlockContainer: ImageRecipeBlockContainer? = null,
    recipeFromImage: () -> Unit,
    updateBlockContainer: (ImageRecipeBlockContainer?) -> Unit,
    closeRecipeFromImagePopup: () -> Unit,
    loadRecipeFromImageResult: (Boolean) -> Unit,
    recipeFromImageBitmap: ImageBitmap?,
    recipeFromImageOrientation: Int = 0,
    recipeFromImageTakeImage: () -> Unit,
    recipeFromImagePickImage: () -> Unit,
    updateBlockType: (ImageRecipeBlock, Int) -> Unit,
    updateBlockElementIndex: (ImageRecipeBlock, Int, Int) -> Unit,
    updateBlockIsCollapsed: (ImageRecipeBlock, Int) -> Unit,
    rotateRecipeFromImage90Right: () -> Unit,
    rotateRecipeFromImage90Left: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    LoadingOverlay(isLoading)

    Scaffold(
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(RecipeCreateDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            RecipeFromWebsiteSection(
                recipeWebsiteUrl = recipeWebsiteUrl,
                validateRecipeWebsiteUrl = validateRecipeWebsiteUrl,
                isRecipeFromWebsiteSectionVisible = isRecipeFromWebsiteSectionVisible,
                updateRecipeWebsiteUrl = updateRecipeWebsiteUrl,
                loadRecipeFromWebsite = loadRecipeFromWebsite,
                showRecipeFromWebsiteSection = showRecipeFromWebsiteSection,
                hideRecipeFromWebsiteSection = hideRecipeFromWebsiteSection
            )

            RecipeFromImageSection(
                isRecipeFromImagePopupOpen = isRecipeFromImagePopupOpen,
                imageRecipeBlockContainer = imageRecipeBlockContainer,
                recipeFromImageBitmap = recipeFromImageBitmap,
                imageOrientation = recipeFromImageOrientation,
                recipeFromImageTakeImage = recipeFromImageTakeImage,
                recipeFromImagePickImage = recipeFromImagePickImage,
                recipeFromImage = recipeFromImage,
                updateBlockContainer = updateBlockContainer,
                closeRecipeFromImagePopup = closeRecipeFromImagePopup,
                loadRecipeFromImageResult = loadRecipeFromImageResult,
                updateBlockType = updateBlockType,
                updateBlockElementIndex = updateBlockElementIndex,
                updateBlockIsCollapsed = updateBlockIsCollapsed,
                rotateRecipeFromImage90Right = rotateRecipeFromImage90Right,
                rotateRecipeFromImage90Left = rotateRecipeFromImage90Left
            )

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
                    .fillMaxWidth(),
                openTagListPopup = openTagListPopup,
                closeTagListPopup = closeTagListPopup,
                isTagListPopupOpen = isTagListPopupOpen,
                tagListFilterName = tagListFilterName,
                tagListUpdateFilterName = tagListUpdateFilterName
            )
        }
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
            tagListUpdateFilterName = {},
            updateRecipeWebsiteUrl = {},
            loadRecipeFromWebsite = {},
            showRecipeFromWebsiteSection = {},
            hideRecipeFromWebsiteSection = {},
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            recipeFromImageBitmap = null,
            recipeFromImageTakeImage = {},
            recipeFromImagePickImage = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
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
            tagListUpdateFilterName = {},
            updateRecipeWebsiteUrl = {},
            loadRecipeFromWebsite = {},
            showRecipeFromWebsiteSection = {},
            hideRecipeFromWebsiteSection = {},
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            recipeFromImageBitmap = null,
            recipeFromImageTakeImage = {},
            recipeFromImagePickImage = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
        )
    }
}

@PhonePreview
@Composable
fun RecipeCreateScreenLoadingPhonePreview() {
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
            tagListUpdateFilterName = {},
            isLoading = true,
            updateRecipeWebsiteUrl = {},
            loadRecipeFromWebsite = {},
            showRecipeFromWebsiteSection = {},
            hideRecipeFromWebsiteSection = {},
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            recipeFromImageBitmap = null,
            recipeFromImageTakeImage = {},
            recipeFromImagePickImage = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
        )
    }
}

@TabletPreview
@Composable
fun RecipeCreateScreenLoadingTabletPreview() {
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
            tagListUpdateFilterName = {},
            isLoading = true,
            updateRecipeWebsiteUrl = {},
            loadRecipeFromWebsite = {},
            showRecipeFromWebsiteSection = {},
            hideRecipeFromWebsiteSection = {},
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            recipeFromImageBitmap = null,
            recipeFromImageTakeImage = {},
            recipeFromImagePickImage = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
        )
    }
}

//endregion