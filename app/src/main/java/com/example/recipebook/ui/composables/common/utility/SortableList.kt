package com.example.recipebook.ui.composables.common.utility

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.SortableList_DeleteIngredient
import com.example.recipebook.ui.theme.SortableList_ItemGoDown
import com.example.recipebook.ui.theme.SortableList_ItemGoUp

@Composable
fun <T> SortableList(
    itemList: List<T>,
    updateList: (List<T>) -> Unit,
    onClickNewItem: () -> Unit,
    newItemButtonIcon: ImageVector,
    @StringRes newItemButtonText: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable() (
        item: T,
        index: Int,
        modifier: Modifier
    ) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        for ((index, item) in itemList.withIndex()) {
            val canGoUp = index > 0
            val canGoDown = index < itemList.size - 1

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = modifier.fillMaxHeight()
                ) {
                    IconButton(
                        onClick = {
                            val newItemList = itemList.toMutableList()

                            newItemList.removeAt(index)
                            newItemList.add(index - 1, item)

                            updateList(newItemList)
                        },
                        modifier = modifier.size(dimensionResource(R.dimen.padding_medium)),
                        enabled = enabled && canGoUp
                    ) {
                        Icon(imageVector = SortableList_ItemGoUp, contentDescription = "")
                    }
                    IconButton(
                        onClick = {
                            val newItemList = itemList.toMutableList()

                            newItemList.removeAt(index)
                            newItemList.add(index + 1, item)

                            updateList(newItemList)
                        },
                        modifier = modifier.size(dimensionResource(R.dimen.padding_medium)),
                        enabled = enabled && canGoDown
                    ) {
                        Icon(imageVector = SortableList_ItemGoDown, contentDescription = "")
                    }
                }

                content(item, index, Modifier.weight(1F))

                IconButton(
                    onClick = {
                        val newItemList = itemList
                            .filterIndexed { curIndex, _ -> curIndex != index }

                        updateList(newItemList)
                    },
                    enabled = enabled
                ) {
                    Icon(imageVector = SortableList_DeleteIngredient, contentDescription = "")
                }
            }
        }

        Button(
            onClick = onClickNewItem
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = newItemButtonIcon, contentDescription = "")
                Text(
                    text = stringResource(id = newItemButtonText),
                    modifier = Modifier.weight(1F)
                )
            }
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun SortableListPreview() {
    RecipeBookTheme {
        SortableList<String>(
            itemList = listOf("AAA", "BBB", "CCC"),
            updateList = {},
            onClickNewItem = {},
            newItemButtonIcon = Icons.Default.Add,
            newItemButtonText = R.string.recipe_ingredients,
            enabled = true
        ) { name, index, modifier ->
            Text(name, modifier)
        }
    }
}

//endregion