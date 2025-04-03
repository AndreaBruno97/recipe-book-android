@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.recipebook.ui.navigation.RecipeBookNavHost
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.TopAppBar_Back

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

//endregion