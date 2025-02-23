
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.commonComposable.recipeFormBody.RecipeFormBody
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RecipeCreateDestination : NavigationDestinationNoParams {
    override val route = "recipe_create"
    override val titleRes = R.string.routeTitle_recipeCreate
}

@Composable
fun RecipeCreateScreen(
    navigateToRecipeDetails: (ObjectId) -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: RecipeCreateViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(RecipeCreateDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ){ innerPadding ->
        RecipeFormBody(
            recipeUiState = viewModel.recipeUiState,
            onRecipeValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    val newRecipeId = viewModel.saveRecipe()

                    if(newRecipeId != null){
                        navigateToRecipeDetails(newRecipeId)
                    }
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )

    }
}