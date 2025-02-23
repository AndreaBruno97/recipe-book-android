
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
import com.example.recipebook.ui.theme.RecipeBookTheme
import org.mongodb.kbson.ObjectId

object HomeDestination: NavigationDestinationNoParams{
    override val route = "home"
    override val titleRes = R.string.routeTitle_home
}

@Composable
fun HomeScreen(
    navigateToRecipeCreate: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

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
            recipeList = homeUiState.recipeList,
            onRecipeClick = navigateToRecipeDetails,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    recipeList: List<Recipe>,
    onRecipeClick: (ObjectId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding))
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
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
    onRecipeClick: (Recipe) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
    items(items = recipeList, key = { it._id.toHexString() }){ recipe ->
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

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview(){
    RecipeBookTheme {
        HomeBody(RecipeExamples.recipeList, onRecipeClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview(){
    RecipeBookTheme {
        HomeBody(listOf(), onRecipeClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeItemPreview(){
    RecipeBookTheme {
        RecipeItem(RecipeExamples.recipe1)
    }
}