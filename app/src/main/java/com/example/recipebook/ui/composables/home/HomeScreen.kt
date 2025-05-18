@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorViewModel
import com.example.recipebook.ui.composables.common.utility.clearCache
import com.example.recipebook.ui.composables.home.internal.HomeRecipeFilters
import com.example.recipebook.ui.composables.home.internal.HomeRecipeList
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
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
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tagListViewModel: TagListSelectorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val localContext = LocalContext.current
    clearCache(localContext)

    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val filterState by homeViewModel.filterState.collectAsState()

    val tagListUiState by tagListViewModel.tagListUiState.collectAsState()
    val tagListFilterState by tagListViewModel.tagListFilterState.collectAsState()

    val usedTagIdList = filterState.filterTagList.map { it._id }
    val unusedTagList = tagListUiState.tagDetailList.filter { it._id !in usedTagIdList }

    HomeScreenStateCollector(
        screenSize = screenSize,
        navigateToRecipeCreate = navigateToRecipeCreate,
        navigateToRecipeDetails = navigateToRecipeDetails,
        navigateToTagList = navigateToTagList,
        loadRecipeImage = homeViewModel::loadRecipeImage,
        recipeList = homeUiState.recipeList,
        modifier = modifier,
        filter = filterState,
        updateFilter = homeViewModel::updateFilter,
        isFilterSectionOpen = homeViewModel.isFilterSectionOpen,
        openFilterSection = homeViewModel::openFilterSection,
        closeFilterSection = homeViewModel::closeFilterSection,
        updateTagSelectorFilterName = tagListViewModel::updateFilterName,
        openTagListPopup = tagListViewModel::openTagListPopup,
        unusedTagList = unusedTagList,
        closeTagListPopup = tagListViewModel::closeTagListPopup,
        isTagListPopupOpen = tagListViewModel.isTagListPopupOpen,
        filterName = tagListFilterState.filterName
    )
}

@Composable
private fun HomeScreenStateCollector(
    screenSize: ScreenSize,
    navigateToRecipeCreate: () -> Unit,
    navigateToRecipeDetails: (ObjectId) -> Unit,
    navigateToTagList: () -> Unit,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    recipeList: List<Recipe>,
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    updateFilter: (RecipeListFilterState) -> Unit,
    isFilterSectionOpen: Boolean = false,
    openFilterSection: () -> Unit,
    closeFilterSection: () -> Unit,
    updateTagSelectorFilterName: (String) -> Unit,
    openTagListPopup: () -> Unit,
    unusedTagList: List<Tag> = listOf(),
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    filterName: String = "",
    enabled: Boolean = true
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
            loadRecipeImage = loadRecipeImage,
            screenSize = screenSize,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            filter = filter,
            updateFilter = updateFilter,
            isFilterSectionOpen = isFilterSectionOpen,
            openFilterSection = openFilterSection,
            closeFilterSection = closeFilterSection,
            updateTagSelectorFilterName = updateTagSelectorFilterName,
            openTagListPopup = openTagListPopup,
            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen,
            filterName = filterName,
            enabled = enabled
        )
    }
}

@Composable
private fun HomeBody(
    recipeList: List<Recipe>,
    screenSize: ScreenSize,
    onRecipeClick: (ObjectId) -> Unit,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding)),
    filter: RecipeListFilterState = RecipeListFilterState(),
    updateFilter: (RecipeListFilterState) -> Unit,
    isFilterSectionOpen: Boolean = false,
    openFilterSection: () -> Unit,
    closeFilterSection: () -> Unit,
    updateTagSelectorFilterName: (String) -> Unit,
    openTagListPopup: () -> Unit,
    unusedTagList: List<Tag> = listOf(),
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    filterName: String = "",
    enabled: Boolean = true
) {
    val columnNum = when (screenSize) {
        ScreenSize.SMALL -> 1
        ScreenSize.MEDIUM -> 2
        ScreenSize.LARGE -> 3
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(contentPadding)
    ) {

        HomeRecipeFilters(
            filter = filter,
            updateFilter = updateFilter,
            isFilterSectionOpen = isFilterSectionOpen,
            openFilterSection = openFilterSection,
            closeFilterSection = closeFilterSection,
            updateTagSelectorFilterName = updateTagSelectorFilterName,
            openTagListPopup = openTagListPopup,
            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen,
            filterName = filterName,
            enabled = enabled
        )

        if (recipeList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_recipes_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            HomeRecipeList(
                recipeList = recipeList,
                columnNum = columnNum,
                onRecipeClick = { onRecipeClick(it._id) },
                loadRecipeImage = loadRecipeImage,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
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
            recipeList = RecipeExamples.recipeList,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
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
            recipeList = RecipeExamples.recipeList,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
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
            recipeList = RecipeExamples.recipeList,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}


@PhonePreview
@Composable
fun HomeBodyPhonePreview() {
    RecipeBookTheme {
        HomeBody(
            recipeList = RecipeExamples.recipeList,
            screenSize = ScreenSize.SMALL,
            onRecipeClick = {},
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

@FoldablePreview
@Composable
fun HomeBodyFoldablePreview() {
    RecipeBookTheme {
        HomeBody(
            recipeList = RecipeExamples.recipeList,
            screenSize = ScreenSize.MEDIUM,
            onRecipeClick = {},
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

@TabletPreview
@Composable
fun HomeBodyTabletPreview() {
    RecipeBookTheme {
        HomeBody(
            recipeList = RecipeExamples.recipeList,
            screenSize = ScreenSize.LARGE,
            onRecipeClick = {},
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

@DefaultPreview
@Composable
fun HomeBodyEmptyListPreview() {
    RecipeBookTheme {
        HomeBody(
            recipeList = listOf(),
            screenSize = ScreenSize.SMALL,
            onRecipeClick = {},
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap },
            updateFilter = {},
            openFilterSection = {},
            closeFilterSection = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

//endregion