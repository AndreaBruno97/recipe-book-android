package com.example.recipebook.ui.composables.home.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorBody
import com.example.recipebook.ui.composables.home.RecipeListFilterState
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.Home_RecipeFilter_ClearFilter
import com.example.recipebook.ui.theme.Home_RecipeFilter_CloseSection
import com.example.recipebook.ui.theme.Home_RecipeFilter_OpenSection
import com.example.recipebook.ui.theme.Home_RecipeFilter_RemoveTag
import com.example.recipebook.ui.theme.RecipeBookTheme


@Composable
fun HomeRecipeFilters(
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
    val internalElementsModifier = Modifier
        .fillMaxWidth()
        .padding(dimensionResource(R.dimen.padding_medium))

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {

        HomeRecipeFiltersTitle(
            isFilterSectionOpen = isFilterSectionOpen,
            openFilterSection = openFilterSection,
            closeFilterSection = closeFilterSection,
            modifier = internalElementsModifier
        )

        if (isFilterSectionOpen) {
            HomeRecipeFiltersName(
                filter = filter,
                updateFilter = updateFilter,
                modifier = internalElementsModifier
            )

            HomeRecipeFiltersFavorite(
                filter = filter,
                updateFilter = updateFilter,
                modifier = internalElementsModifier
            )

            HomeRecipeFiltersTag(
                filter = filter,
                updateFilter = updateFilter,
                modifier = internalElementsModifier,
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
}

@Composable
private fun HomeRecipeFiltersTitle(
    modifier: Modifier = Modifier,
    isFilterSectionOpen: Boolean = false,
    openFilterSection: () -> Unit,
    closeFilterSection: () -> Unit
) {

    val filterTitleIcon = if (isFilterSectionOpen) {
        Home_RecipeFilter_CloseSection
    } else {
        Home_RecipeFilter_OpenSection
    }

    val filterTitleAction = if (isFilterSectionOpen) {
        closeFilterSection
    } else {
        openFilterSection
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.home_filterSection_title),
            modifier = Modifier.weight(1F)
        )
        IconButton(
            onClick = filterTitleAction
        ) {
            Icon(imageVector = filterTitleIcon, contentDescription = "")
        }
    }

}

@Composable
fun HomeRecipeFiltersName(
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    updateFilter: (RecipeListFilterState) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = filter.filterName,
            onValueChange = { updateFilter(filter.copy(filterName = it)) },
            modifier = Modifier.weight(1F),
            label = { Text(stringResource(R.string.recipe_name)) }
        )
        IconButton(
            onClick = { updateFilter(filter.copy(filterName = "")) }
        ) {
            Icon(
                imageVector = Home_RecipeFilter_ClearFilter,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun HomeRecipeFiltersFavorite(
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    updateFilter: (RecipeListFilterState) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(stringResource(R.string.recipe_isFavorite))

        Spacer(
            modifier = Modifier
                .width(dimensionResource(R.dimen.padding_medium))
        )

        Switch(
            checked = filter.filterIsFavorite,
            onCheckedChange = { updateFilter(filter.copy(filterIsFavorite = it)) }
        )
    }
}

@Composable
fun HomeRecipeFiltersTag(
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    updateFilter: (RecipeListFilterState) -> Unit,
    updateTagSelectorFilterName: (String) -> Unit,
    openTagListPopup: () -> Unit,
    unusedTagList: List<Tag> = listOf(),
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    filterName: String = "",
    enabled: Boolean = true
) {

    Column(
        modifier = modifier
    ) {
        for ((index, tag) in filter.filterTagList.withIndex()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = tag.name)
                IconButton(
                    onClick = {
                        updateFilter(
                            filter.copy(
                                filterTagList = filter
                                    .filterTagList
                                    .filterIndexed { curIndex, _ ->
                                        curIndex != index
                                    }
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Home_RecipeFilter_RemoveTag,
                        contentDescription = ""
                    )
                }
            }
        }
        Button(
            onClick = {
                updateTagSelectorFilterName("")
                openTagListPopup()
            }
        ) {
            Text(stringResource(R.string.home_filterSection_addTag))
        }

        TagListSelectorBody(
            unusedTagList = unusedTagList,
            closeTagListPopup = closeTagListPopup,
            isTagListPopupOpen = isTagListPopupOpen,
            filterName = filterName,
            enabled = enabled,
            updateFilterName = updateTagSelectorFilterName,
            onTagSelect = {
                updateFilter(
                    filter.copy(
                        filterTagList = filter
                            .filterTagList
                            .plus(it)
                    )
                )
            }
        )
    }
}

//region Preview

@DefaultPreview
@Composable
fun HomeRecipeFiltersPreview() {
    RecipeBookTheme {
        HomeRecipeFilters(
            filter = RecipeListFilterState(
                filterTagList = TagExamples.tagList
            ),
            updateFilter = {},
            isFilterSectionOpen = true,
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
fun HomeRecipeFiltersClosedPreview() {
    RecipeBookTheme {
        HomeRecipeFilters(
            updateFilter = {},
            isFilterSectionOpen = false,
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
fun HomeRecipeFiltersTitleOpenPreview() {
    RecipeBookTheme {
        HomeRecipeFiltersTitle(
            isFilterSectionOpen = true,
            openFilterSection = {},
            closeFilterSection = {}
        )
    }
}

@DefaultPreview
@Composable
fun HomeRecipeFiltersTitleClosedPreview() {
    RecipeBookTheme {
        HomeRecipeFiltersTitle(
            isFilterSectionOpen = false,
            openFilterSection = {},
            closeFilterSection = {}
        )
    }
}

@DefaultPreview
@Composable
fun HomeRecipeFiltersNamePreview() {
    RecipeBookTheme {
        HomeRecipeFiltersName(
            updateFilter = {}
        )
    }
}

@DefaultPreview
@Composable
fun HomeRecipeFiltersFavoritePreview() {
    RecipeBookTheme {
        HomeRecipeFiltersFavorite(
            updateFilter = {}
        )
    }
}

@DefaultPreview
@Composable
fun HomeRecipeFiltersTagPreview() {
    RecipeBookTheme {
        HomeRecipeFiltersTag(
            updateFilter = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

//endregion