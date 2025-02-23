
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.recipeDetails

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.recipe.Recipe
import com.example.recipebook.data.recipe.RecipeExamples
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.navigation.NavigationDestination
import com.example.recipebook.ui.navigation.NavigationDestinationRecipeId
import com.example.recipebook.ui.theme.RecipeBookTheme
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RecipeDetailsDestination: NavigationDestinationRecipeId{
    override val route = "recipe_details"
    override val titleRes = R.string.routeTitle_recipeDetails
    const val recipeIdArg = "recipeId"
    val routeWithArgs = "$route/{$recipeIdArg}"
}

@Composable
fun RecipeDetailsScreen(
    navigateToEditRecipe: (ObjectId) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
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
                onClick = {navigateToEditRecipe(uiState.value.recipe._id)},
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
    ){ innerPadding ->
        RecipeDetailsBody(
            recipeDetailsUiState = uiState.value,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteRecipe()
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

@Composable
fun RecipeDetailsBody(
    recipeDetailsUiState: RecipeDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ){
        var deleteConfirmationReqired by rememberSaveable { mutableStateOf(false) }

        RecipeDetails(
            recipe = recipeDetailsUiState.recipe,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = {deleteConfirmationReqired = true},
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete_button_text))
        }

        if(deleteConfirmationReqired){
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationReqired = false
                    onDelete()
                },
                onDeleteCancel = {deleteConfirmationReqired = false},
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun RecipeDetails(
    recipe: Recipe, modifier: Modifier = Modifier
){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ){
        RecipeDetailsRow(
            labelResId = R.string.recipe_name,
            recipeDetail = recipe.name,
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium)
            )
        )
        RecipeDetailsRow(
            labelResId = R.string.recipe_id,
            recipeDetail = recipe._id.toHexString(),
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium)
            )
        )
    }
}

@Composable
private fun RecipeDetailsRow(
    @StringRes labelResId: Int,
    recipeDetail: String,
    modifier: Modifier = Modifier
){
    Row(modifier){
       Text(stringResource(labelResId))
       Spacer(modifier = Modifier.weight(1f))
       Text(text = recipeDetail, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
){
    AlertDialog (
        onDismissRequest = { /* Do Nothing */ },
        title = {Text(stringResource(R.string.deletePopup_title))},
        text = {Text(stringResource(R.string.deletePopup_text))},
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel){
                Text(stringResource(R.string.confirmationButton_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.confirmationButton_confirm))
            }
        }

    )
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsScreenPreview(){
    RecipeBookTheme {
        RecipeDetailsBody(
            RecipeDetailsUiState(RecipeExamples.recipe1),
            onDelete = {}
        )
    }
}