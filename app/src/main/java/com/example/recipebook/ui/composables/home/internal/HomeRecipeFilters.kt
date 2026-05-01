@file:OptIn(ExperimentalLayoutApi::class)

package com.example.recipebook.ui.composables.home.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.IngredientExamples
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.composables.common.tagListSelector.TagListSelectorBody
import com.example.recipebook.ui.composables.common.utility.ClearableItem
import com.example.recipebook.ui.composables.common.utility.TagChip
import com.example.recipebook.ui.composables.common.utility.TextInput
import com.example.recipebook.ui.composables.home.RecipeListFilterState
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.Chip_Close
import com.example.recipebook.ui.theme.Home_RecipeFilter_AddIngredient
import com.example.recipebook.ui.theme.RecipeBookTheme


@Composable
fun HomeRecipeFilters(
    modifier: Modifier = Modifier,
    filter: RecipeListFilterState = RecipeListFilterState(),
    screenSize: ScreenSize,
    updateFilter: (RecipeListFilterState) -> Unit,
    updateTagSelectorFilterName: (String) -> Unit,
    openTagListPopup: () -> Unit,
    closeFilterPopup: () -> Unit,
    unusedTagList: List<Tag> = listOf(),
    closeTagListPopup: () -> Unit,
    isTagListPopupOpen: Boolean = false,
    filterName: String = "",
    enabled: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        HomeRecipeFiltersName(
            filter = filter,
            updateFilter = updateFilter,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        HomeRecipeFiltersFavorite(
            filter = filter,
            updateFilter = updateFilter
        )

        HomeRecipeFiltersTag(
            screenSize = screenSize,
            filter = filter,
            updateFilter = updateFilter,
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,

            ) {
            OutlinedButton(
                onClick = {
                    updateFilter(RecipeListFilterState())
                },
                modifier = Modifier.weight(0.8F)
            ) {
                Text(stringResource(R.string.home_filterSection_clearFilters))
            }

            Spacer(
                modifier = Modifier
                    .width(dimensionResource(R.dimen.padding_medium))
            )

            Button(
                onClick = closeFilterPopup,
                modifier = Modifier.weight(0.8F)
            ) {
                Text(stringResource(R.string.home_filterSection_showRecipes))
            }
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
    Column(
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.home_filterSection_name)
        )

        ClearableItem(
            modifier = modifier,
            clearItem = { updateFilter(filter.copy(filterName = "")) }
        ) { clearableItemModifier ->
            TextInput(
                value = filter.filterName,
                onValueChange = { updateFilter(filter.copy(filterName = it)) },
                enabled = enabled,
                modifier = clearableItemModifier,
                labelText = stringResource(R.string.recipe_name)
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
        Text(stringResource(R.string.home_filterSection_isFavorite))

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
    screenSize: ScreenSize,
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
        Text(stringResource(R.string.home_filterSection_tagTitle))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    updateTagSelectorFilterName("")
                    openTagListPopup()
                },
                modifier = Modifier.fillMaxWidth(0.8F)
            ) {
                Text(stringResource(R.string.home_filterSection_addTag))
            }
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            for ((index, tag) in filter.filterTagList.withIndex()) {
                TagChip(
                    tag = tag.toTagDao(),
                    showCloseIcon = true,
                    onClick = {
                        updateFilter(filter.copy(filterTagList =
                        filter
                            .filterTagList
                            .filterIndexed { curIndex, _ ->
                                curIndex != index
                            }
                        ))
                    },
                    modifier = Modifier
                        .widthIn(max = dimensionResource(R.dimen.home_filters_max_tag_width))
                )
            }
        }

        TagListSelectorBody(
            screenSize = screenSize,
            unusedTagList = unusedTagList,
            selectedTagList = filter.filterTagList,
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
            },
            onTagRemoval = { tagToRemove ->
                updateFilter(filter.copy(filterTagList =
                filter
                    .filterTagList
                    .filter { tag ->
                        tag._id != tagToRemove._id
                    }
                ))
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
        Text(stringResource(R.string.home_filterSection_ingredientTitle))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ClearableItem(
                modifier = Modifier.weight(1F),
                clearItem = { updateFilter(filter.copy(filterInputIngredient = "")) }
            ) {
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

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            for ((index, ingredient) in filter.filterIngredientList.withIndex()) {
                InputChip(
                    onClick = {
                        updateFilter(filter.copy(filterIngredientList =
                        filter
                            .filterIngredientList
                            .filterIndexed { curIndex, _ ->
                                curIndex != index
                            }
                        ))
                    },
                    selected = false,
                    label = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = ingredient
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Chip_Close,
                            contentDescription = "",
                            Modifier.size(InputChipDefaults.AvatarSize)
                        )
                    }
                )
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
            screenSize = ScreenSize.SMALL,
            filter = RecipeListFilterState(
                filterTagList = listOf(
                    TagExamples.tag1,
                    TagExamples.longTag,
                    TagExamples.tag2
                ),
                filterIngredientList = listOf(
                    IngredientExamples.ingredientA.name,
                    IngredientExamples.ingredientB.name,
                    IngredientExamples.ingredientLong.name
                )
            ),
            updateFilter = {},
            updateTagSelectorFilterName = {},
            openTagListPopup = {},
            closeTagListPopup = {},
            closeFilterPopup = {}
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
            screenSize = ScreenSize.SMALL,
            filter = RecipeListFilterState(
                filterTagList = TagExamples.tagListWithLong
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
                filterIngredientList = IngredientExamples.ingredientListWithLong.map { it.name }
            ),
            updateFilter = {}
        )
    }
}

//endregion