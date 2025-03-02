
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.recipe.Recipe
import com.example.recipebook.data.recipe.RecipeExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.navigation.NavigationDestination
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.FoldablePreview
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import org.mongodb.kbson.ObjectId

object HomeDestination: NavigationDestinationNoParams{
    override val route = "home"
    override val titleRes = R.string.routeTitle_home
}

@Composable
fun HomeScreen(
    screenSize: ScreenSize,
    navigateToRecipeCreate: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    HomeScreenStateCollector(
        screenSize,
        navigateToRecipeCreate,
        navigateToRecipeDetails,
        homeUiState.recipeList,
        modifier
    )
}


@Composable
private fun HomeScreenStateCollector(
    screenSize: ScreenSize,
    navigateToRecipeCreate: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    recipeList: List<Recipe>,
    modifier: Modifier = Modifier
) {
    val scrollBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
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
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.recipe_create_icon_name)
                )
            }
        }
    ){ innerPadding ->
        HomeBody(
            recipeList = recipeList,
            onRecipeClick = navigateToRecipeDetails,
            screenSize = screenSize,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    recipeList: List<Recipe>,
    screenSize: ScreenSize,
    onRecipeClick: (ObjectId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding))
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        val columnNum = when(screenSize){
            ScreenSize.SMALL -> 1
            ScreenSize.MEDIUM -> 2
            ScreenSize.LARGE -> 3
        }

        if(recipeList.isEmpty()){
            Text(
                text = stringResource(R.string.no_recipes_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            RecipeList(
                recipeList = recipeList,
                columnNum = columnNum,
                onRecipeClick = { onRecipeClick(it._id) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun RecipeList(
    recipeList: List<Recipe>,
    columnNum: Int,
    onRecipeClick: (Recipe) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnNum),
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(recipeList){ recipe ->
            RecipeItem(
                recipe = recipe,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onRecipeClick(recipe) }
            )
        }
    }
}

@Composable
private fun RecipeItem(
    recipe: Recipe, modifier: Modifier = Modifier
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation))
    ){
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = recipe._id.toHexString(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@PhonePreview
@Composable
fun HomeScreenPhonePreview(){
    RecipeBookTheme {
        HomeScreenStateCollector(
            ScreenSize.SMALL,
            navigateToRecipeCreate = {},
            navigateToRecipeDetails = {},
            RecipeExamples.recipeList,
        )
    }
}

@FoldablePreview
@Composable
fun HomeScreenFoldablePreview(){
    RecipeBookTheme {
        HomeScreenStateCollector(
            ScreenSize.MEDIUM,
            navigateToRecipeCreate = {},
            navigateToRecipeDetails = {},
            RecipeExamples.recipeList,
        )
    }
}

@TabletPreview
@Composable
fun HomeScreenTabletPreview(){
    RecipeBookTheme {
        HomeScreenStateCollector(
            ScreenSize.LARGE,
            navigateToRecipeCreate = {},
            navigateToRecipeDetails = {},
            RecipeExamples.recipeList,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview(){
    RecipeBookTheme {
        HomeBody(
            recipeList = RecipeExamples.recipeList,
            screenSize = ScreenSize.SMALL,
            onRecipeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview(){
    RecipeBookTheme {
        HomeBody(
            recipeList = listOf(),
            screenSize = ScreenSize.SMALL,
            onRecipeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeItemPreview(){
    RecipeBookTheme {
        RecipeItem(RecipeExamples.recipe1)
    }
}