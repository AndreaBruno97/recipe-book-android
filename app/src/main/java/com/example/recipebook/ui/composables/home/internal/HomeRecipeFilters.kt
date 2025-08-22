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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.IngredientExamples
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorBody
import com.example.recipebook.ui.composables.common.utility.ClearableItem
import com.example.recipebook.ui.composables.common.utility.CollapsableSection
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.composables.home.RecipeListFilterState
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.Home_RecipeFilter_AddIngredient
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

    CollapsableSection(
        isCollapsed = (isFilterSectionOpen == false),
        title = stringResource(R.string.home_filterSection_title),
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        collapseSection = closeFilterSection,
        expandSection = openFilterSection
    ) {
        HomeRecipeFiltersName(
            filter = filter,
            updateFilter = updateFilter,
            enabled = enabled,
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

        HomeRecipeFiltersIngredients(
            filter = filter,
            updateFilter = updateFilter,
            enabled = enabled,
            modifier = internalElementsModifier
        )

        Button(
            onClick = {
                updateFilter(RecipeListFilterState())
            }
        ) {
            Text(stringResource(R.string.home_filterSection_clearFilters))
        }
    }
}

@Composable
fun HomeRecipeFiltersName(
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    enabled: Boolean = true,
    updateFilter: (RecipeListFilterState) -> Unit
) {
    ClearableItem(
        modifier = modifier,
        clearItem = { updateFilter(filter.copy(filterName = "")) }
    ){ clearableItemModifier ->
        TextInput(
            value = filter.filterName,
            onValueChange = { updateFilter(filter.copy(filterName = it)) },
            enabled = enabled,
            modifier = clearableItemModifier,
            labelText = stringResource(R.string.recipe_name)
        )
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
            ClearableItem(
                modifier = Modifier.fillMaxWidth(),
                clearItem = {
                    updateFilter(filter.copy(filterTagList =
                        filter
                        .filterTagList
                        .filterIndexed { curIndex, _ ->
                            curIndex != index
                        }
                    ))
                }
            ) { clearableItemModifier ->
                Text(text = tag.name)
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

@Composable
fun HomeRecipeFiltersIngredients(
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    enabled: Boolean = true,
    updateFilter: (RecipeListFilterState) -> Unit
) {
    Column(modifier = modifier) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ClearableItem(
                modifier = Modifier.weight(1F),
                clearItem = { updateFilter(filter.copy(filterInputIngredient = "")) }
            ){
                TextInput(
                    value = filter.filterInputIngredient,
                    onValueChange = { updateFilter(filter.copy(filterInputIngredient = it)) },
                    enabled = true,
                    modifier = Modifier.weight(1F),
                    labelText = stringResource(R.string.home_filterSection_ingredient)
                )
            }

            IconButton(
                onClick = {
                    updateFilter(
                        filter.copy(
                            filterInputIngredient = "",
                            filterIngredientList = filter.filterIngredientList.plus(filter.filterInputIngredient)
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = Home_RecipeFilter_AddIngredient,
                    contentDescription = ""
                )
            }
        }

        for ((index, ingredient) in filter.filterIngredientList.withIndex()) {
            ClearableItem(
                modifier = Modifier.fillMaxWidth(),
                clearItem = {
                    updateFilter(filter.copy(filterIngredientList =
                    filter
                        .filterIngredientList
                        .filterIndexed { curIndex, _ ->
                            curIndex != index
                        }
                    ))
                }
            ) {
                Text(text = ingredient)
            }
        }
    }
}

//region Preview

@DefaultPreview
@Composable
fun HomeRecipeFiltersPreview() {
    RecipeBookTheme {
        HomeRecipeFilters(
            filter = RecipeListFilterState(
                filterTagList = listOf(
                    TagExamples.tag1,
                    TagExamples.tag2
                ),
                filterIngredientList = listOf(
                    IngredientExamples.ingredientA.name,
                    IngredientExamples.ingredientB.name
                )
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
            filter = RecipeListFilterState(
                filterTagList = TagExamples.tagList
            ),
            updateFilter = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {}
        )
    }
}

@DefaultPreview
@Composable
fun HomeRecipeFiltersIngredientsPreview() {
    RecipeBookTheme {
        HomeRecipeFiltersIngredients(
            filter = RecipeListFilterState(
                filterIngredientList = IngredientExamples.ingredientList.map { it.name }
            ),
            updateFilter = {}
        )
    }
}

//endregion