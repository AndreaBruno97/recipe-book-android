@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.recipebook.ui.navigation.RecipeBookNavHost
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.TopAppBar_Back
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.recipebook.ui.theme.BottomAppBar_BackupManager
import com.example.recipebook.ui.theme.BottomAppBar_CreateRecipe
import com.example.recipebook.ui.theme.BottomAppBar_TagList

@Composable
fun RecipeBookApp(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    RecipeBookNavHost(windowSize = windowSize, modifier = modifier, navController = navController)
}

@Composable
fun RecipeBookTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = TopAppBar_Back,
                        contentDescription = stringResource(R.string.topAppBar_back)
                    )
                }
            }
        }
    )
}

@Composable
fun RecipeBookBottomAppBar(
    modifier: Modifier = Modifier,
    navigateToTagList: () -> Unit,
    navigateToBackupManager: () -> Unit,
    navigateToRecipeCreate: () -> Unit
){
    BottomAppBar(
        modifier = modifier
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ){
            RecipeBookBottomAppBarButton(
                modifier = Modifier.weight(1F),
                text = stringResource(R.string.home_navigate_to_tag_list),
                icon = BottomAppBar_TagList,
                onClick = navigateToTagList
            )

            RecipeBookBottomAppBarButton(
                modifier = Modifier.weight(1F),
                text = stringResource(R.string.home_navigate_to_backup_manager),
                icon = BottomAppBar_BackupManager,
                onClick = navigateToBackupManager
            )

            RecipeBookBottomAppBarButton(
                modifier = Modifier.weight(1F),
                text = stringResource(R.string.recipe_create_icon_name),
                icon = BottomAppBar_CreateRecipe,
                onClick = navigateToRecipeCreate
            )
        }
    }
}

@Composable
fun RecipeBookBottomAppBarButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
){
    TextButton(
        onClick = onClick,
        modifier = modifier
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = "")
            Text(text)
        }
    }
}

//region Preview

@DefaultPreview
@Composable
fun RecipeBookTopAppBarPreview() {
    RecipeBookTheme {
        RecipeBookTopAppBar(
            title = "Test Title",
            canNavigateBack = true,
            navigateUp = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeBookTopAppBarNoBackPreview() {
    RecipeBookTheme {
        RecipeBookTopAppBar(
            title = "Test Title",
            canNavigateBack = false,
            navigateUp = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeBookBottomAppBarPreview() {
    RecipeBookTheme {
        RecipeBookBottomAppBar(
            navigateToTagList = {},
            navigateToBackupManager = {},
            navigateToRecipeCreate = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeBookBottomAppBarButtonPreview() {
    RecipeBookTheme {
        RecipeBookBottomAppBarButton(
            text= "Example",
            icon = Icons.Default.ThumbUp,
            onClick = {}
        )
    }
}

//endregion