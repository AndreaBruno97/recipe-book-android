
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Icon
import androidx.compose.ui.res.stringResource
import com.example.recipebook.ui.navigation.RecipeBookNavHost
import com.example.recipebook.ui.theme.TopAppBar_Back

@Composable
fun RecipeBookApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    RecipeBookNavHost(modifier = modifier, navController = navController)
}

@Composable
fun RecipeBookTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
){
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp){
                    Icon(
                        imageVector = TopAppBar_Back,
                        contentDescription = stringResource(R.string.topAppBar_back)
                    )
                }
            }
        }
    )
}