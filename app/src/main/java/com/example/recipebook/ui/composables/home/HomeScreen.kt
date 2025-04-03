@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.home.internal.HomeBody
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.FoldablePreview
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.Home_FabAddRecipe
import com.example.recipebook.ui.theme.Home_FabTagList
import com.example.recipebook.ui.theme.RecipeBookTheme
import org.mongodb.kbson.ObjectId

object HomeDestination : NavigationDestinationNoParams {
    override val route = "home"
    override val titleRes = R.string.routeTitle_home
}

@Composable
fun HomeScreen(
    screenSize: ScreenSize,
    navigateToRecipeCreate: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    navigateToTagList: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    HomeScreenStateCollector(
        screenSize,
        navigateToRecipeCreate,
        navigateToRecipeDetails,
        navigateToTagList,
        homeUiState.recipeList,
        modifier
    )
}

@Composable
private fun HomeScreenStateCollector(
    screenSize: ScreenSize,
    navigateToRecipeCreate: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    navigateToTagList: () -> Unit,
    recipeList: List<Recipe>,
    modifier: Modifier = Modifier
) {
    val scrollBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBarBehavior.nestedScrollConnection),
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBarBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToRecipeCreate,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Home_FabAddRecipe,
                    contentDescription = stringResource(R.string.recipe_create_icon_name)
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Button(
                        onClick = navigateToTagList
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Home_FabTagList, contentDescription = "")
                            Text(stringResource(R.string.home_navigate_to_tag_list))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        HomeBody(
            recipeList = recipeList,
            onRecipeClick = navigateToRecipeDetails,
            screenSize = screenSize,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

//region Preview

@PhonePreview
@Composable
fun HomeScreenPhonePreview() {
    RecipeBookTheme {
        HomeScreenStateCollector(
            ScreenSize.SMALL,
            navigateToRecipeCreate = {},
            navigateToRecipeDetails = {},
            navigateToTagList = {},
            RecipeExamples.recipeList,
        )
    }
}

@FoldablePreview
@Composable
fun HomeScreenFoldablePreview() {
    RecipeBookTheme {
        HomeScreenStateCollector(
            ScreenSize.MEDIUM,
            navigateToRecipeCreate = {},
            navigateToRecipeDetails = {},
            navigateToTagList = {},
            RecipeExamples.recipeList,
        )
    }
}

@TabletPreview
@Composable
fun HomeScreenTabletPreview() {
    RecipeBookTheme {
        HomeScreenStateCollector(
            ScreenSize.LARGE,
            navigateToRecipeCreate = {},
            navigateToRecipeDetails = {},
            navigateToTagList = {},
            RecipeExamples.recipeList,
        )
    }
}

//endregion