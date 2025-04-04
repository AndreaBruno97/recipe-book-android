@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.recipeDetails


import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.recipeDetails.internal.RecipeDetailsBody
import com.example.recipebook.ui.navigation.NavigationDestinationRecipeId
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RecipeDetailsDestination : NavigationDestinationRecipeId {
    override val route = "recipe_details"
    override val titleRes = R.string.routeTitle_recipeDetails
    const val recipeIdArg = "recipeId"
    val routeWithArgs = "$route/{$recipeIdArg}"
}

@Composable
fun RecipeDetailsScreen(
    screenSize: ScreenSize,
    navigateToEditRecipe: (ObjectId) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()

    RecipeDetailsScreenStateCollector(
        screenSize = screenSize,
        navigateToEditRecipe = navigateToEditRecipe,
        navigateBack = navigateBack,
        modifier = modifier,
        uiState = uiState.value,
        deleteRecipe = viewModel::deleteRecipe,
        isDeletePopupOpen = viewModel.isDeletePopupOpen,
        openDeletePopup = viewModel::openDeletePopup,
        closeDeletePopup = viewModel::closeDeletePopup
    )
}

@Composable
fun RecipeDetailsScreenStateCollector(
    screenSize: ScreenSize,
    navigateToEditRecipe: (ObjectId) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: RecipeDetailsUiState,
    deleteRecipe: suspend () -> Unit,
    isDeletePopupOpen: Boolean = false,
    openDeletePopup: () -> Unit,
    closeDeletePopup: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(RecipeDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditRecipe(uiState.recipe._id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_recipe_button_text)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        RecipeDetailsBody(
            recipeDetailsUiState = uiState,
            onDelete = {
                coroutineScope.launch {
                    deleteRecipe()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState()),
            isDeletePopupOpen = isDeletePopupOpen,
            openDeletePopup = openDeletePopup,
            closeDeletePopup = closeDeletePopup
        )
    }
}

//region Preview

@PhonePreview
@Composable
fun RecipeDetailsScreenPhonePreview() {
    RecipeBookTheme {
        RecipeDetailsScreenStateCollector(
            ScreenSize.SMALL,
            navigateToEditRecipe = {},
            navigateBack = {},
            uiState = RecipeDetailsUiState(RecipeExamples.recipe1),
            deleteRecipe = {},
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

@TabletPreview
@Composable
fun RecipeDetailsScreenTabletPreview() {
    RecipeBookTheme {
        RecipeDetailsScreenStateCollector(
            ScreenSize.LARGE,
            navigateToEditRecipe = {},
            navigateBack = {},
            uiState = RecipeDetailsUiState(RecipeExamples.recipe1),
            deleteRecipe = {},
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

@PhonePreview
@Composable
fun DeletePopupPhonePreview() {
    RecipeBookTheme {
        RecipeDetailsScreenStateCollector(
            ScreenSize.SMALL,
            navigateToEditRecipe = {},
            navigateBack = {},
            uiState = RecipeDetailsUiState(RecipeExamples.recipe1),
            deleteRecipe = {},
            isDeletePopupOpen = true,
            openDeletePopup = {},
            closeDeletePopup = {}
        )
    }
}

//endregion