@file:OptIn(ExperimentalLayoutApi::class)

package com.example.recipebook.ui.composables.recipeCreate.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.ui.composables.recipeCreate.ImageRecipeBlock
import com.example.recipebook.ui.composables.recipeCreate.ImageRecipeBlockContainer
import com.example.recipebook.ui.composables.recipeCreate.ImageRecipeBlockTypes
import com.example.recipebook.ui.composables.recipeCreate.testBlockContainer
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeFromImage_HideRow
import com.example.recipebook.ui.theme.RecipeFromImage_ShowRow

@Composable
fun RecipeFromImageSectionRowList(
    modifier: Modifier = Modifier,
    imageRecipeBlockContainer: ImageRecipeBlockContainer = ImageRecipeBlockContainer(),
    updateBlockType: (ImageRecipeBlock, Int) -> Unit,
    updateBlockElementIndex: (ImageRecipeBlock, Int, Int) -> Unit,
    updateBlockIsCollapsed: (ImageRecipeBlock, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {

        itemsIndexed(imageRecipeBlockContainer.imageRecipeBlockList) { blockIndex, block ->
            RecipeFromImageSectionRow(
                modifier = Modifier.fillMaxWidth(),
                block = block,
                blockIndex = blockIndex,
                updateBlockType = updateBlockType,
                updateBlockElementIndex = updateBlockElementIndex,
                updateBlockIsCollapsed = updateBlockIsCollapsed
            )
        }

    }
}

@Composable
private fun RecipeFromImageSectionRow(
    modifier: Modifier = Modifier,
    block: ImageRecipeBlock,
    blockIndex: Int,
    updateBlockType: (ImageRecipeBlock, Int) -> Unit,
    updateBlockElementIndex: (ImageRecipeBlock, Int, Int) -> Unit,
    updateBlockIsCollapsed: (ImageRecipeBlock, Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        val checkboxColor = if (block.blockType == null) {
            CheckboxDefaults.colors()
        } else {
            CheckboxDefaults.colors(
                checkedColor = block.blockType.color
            )
        }

        val blockLineNum = if (block.isCollapsed) {
            1
        } else {
            Int.MAX_VALUE
        }

        //region Title

        if (block.blockType != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(block.blockType.displayName),
                    color = block.blockType.color
                )
            }
        }

        //endregion

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = block.blockType != null,
                colors = checkboxColor,
                onCheckedChange = { updateBlockType(block, blockIndex) }
            )
            if (block.blockType == ImageRecipeBlockTypes.INGREDIENT) {
                RecipeFromImageIngredientRowText(
                    modifier = Modifier.weight(1F),
                    block = block,
                    blockIndex = blockIndex,
                    lineNumber = blockLineNum,
                    updateBlockElementIndex = updateBlockElementIndex
                )
            } else {
                RecipeFromImageDefaultRowText(
                    modifier = Modifier.weight(1F),
                    text = block.text,
                    lineNumber = blockLineNum
                )
            }

            IconButton(
                onClick = { updateBlockIsCollapsed(block, blockIndex) }
            ) {
                Icon(
                    imageVector = if (block.isCollapsed) {
                        RecipeFromImage_ShowRow
                    } else {
                        RecipeFromImage_HideRow
                    },
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
private fun RecipeFromImageDefaultRowText(
    modifier: Modifier = Modifier,
    text: String,
    lineNumber: Int
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = lineNumber,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun RecipeFromImageIngredientRowText(
    modifier: Modifier = Modifier,
    block: ImageRecipeBlock,
    blockIndex: Int,
    lineNumber: Int,
    updateBlockElementIndex: (ImageRecipeBlock, Int, Int) -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement
            .spacedBy(dimensionResource(R.dimen.padding_small)),
        maxLines = lineNumber,
    ) {
        for ((elementIndex, element) in block.elementList.withIndex()) {
            var textModifier = Modifier
                .border(1.dp, Color.LightGray)
                .clickable {
                    updateBlockElementIndex(block, elementIndex, blockIndex)
                }

            if (elementIndex == block.quantityElementIndex) {
                textModifier = textModifier
                    .background(Color.LightGray)
            }

            Text(
                text = element,
                modifier = textModifier
            )
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun RecipeFromImageSectionRowListPreview() {
    RecipeBookTheme {
        RecipeFromImageSectionRowList(
            imageRecipeBlockContainer = testBlockContainer,
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ }
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromImageSectionRowPreview() {
    RecipeBookTheme {
        RecipeFromImageSectionRow(
            block = testBlockContainer.imageRecipeBlockList[1],
            blockIndex = 1,
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ }
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromImageSectionRowExpandedPreview() {
    RecipeBookTheme {
        RecipeFromImageSectionRow(
            block = testBlockContainer.imageRecipeBlockList[1]
                .copy(isCollapsed = false),
            blockIndex = 1,
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ }
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromImageSectionIngredientRowPreview() {
    RecipeBookTheme {
        RecipeFromImageSectionRow(
            block = testBlockContainer.imageRecipeBlockList[4],
            blockIndex = 4,
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ }
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromImageSectionIngredientRowExpandedPreview() {
    RecipeBookTheme {
        RecipeFromImageSectionRow(
            block = testBlockContainer.imageRecipeBlockList[4]
                .copy(isCollapsed = false),
            blockIndex = 4,
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ }
        )
    }
}

//endregion