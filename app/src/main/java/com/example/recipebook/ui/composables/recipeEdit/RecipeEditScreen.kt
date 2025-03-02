
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.recipeEdit

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.recipe.RecipeExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeDetails
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeFormBody
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.toRecipeDetails
import com.example.recipebook.ui.composables.recipeCreate.RecipeCreateScreenStateCollector
import com.example.recipebook.ui.navigation.NavigationDestinationRecipeId
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsDestination
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import kotlinx.coroutines.launch

object RecipeEditDestination: NavigationDestinationRecipeId{
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
    modifier: Modifier = Modifier,
    viewModel: RecipeEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    RecipeEditScreenStateCollector(
        screenSize = screenSize,
        navigateBack = navigateBack,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
        recipeUiState = viewModel.recipeUiState,
        onRecipeValueChange = viewModel::updateUiState,
        updateRecipe = viewModel::updateRecipe
    )
}

@Composable
fun RecipeEditScreenStateCollector(
    screenSize: ScreenSize,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    recipeUiState: RecipeUiState,
    onRecipeValueChange: (RecipeDetails) -> Unit,
    updateRecipe: suspend () -> Unit
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
    ){ innerPadding ->
        RecipeFormBody(
            recipeUiState = recipeUiState,
            onRecipeValueChange = onRecipeValueChange,
            onSaveClick = {
                coroutineScope.launch {
                    updateRecipe()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@PhonePreview
@Composable
fun RecipeEditScreenPhonePreview(){
    RecipeBookTheme {
        RecipeEditScreenStateCollector(
            ScreenSize.SMALL,
            navigateBack = {},
            onNavigateUp = {},
            recipeUiState = RecipeUiState(RecipeExamples.recipe1.toRecipeDetails()),
            onRecipeValueChange = {},
            updateRecipe = {}
        )
    }
}

@TabletPreview
@Composable
fun RecipeEditScreenTabletPreview(){
    RecipeBookTheme {
        RecipeEditScreenStateCollector(
            ScreenSize.LARGE,
            navigateBack = {},
            onNavigateUp = {},
            recipeUiState = RecipeUiState(RecipeExamples.recipe1.toRecipeDetails()),
            onRecipeValueChange = {},
            updateRecipe = {}
        )
    }
}