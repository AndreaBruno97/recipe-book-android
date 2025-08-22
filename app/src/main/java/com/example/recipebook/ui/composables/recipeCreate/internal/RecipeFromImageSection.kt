@file:OptIn(ExperimentalLayoutApi::class)

package com.example.recipebook.ui.composables.recipeCreate.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.composables.common.utility.CardDialog
import com.example.recipebook.ui.composables.recipeCreate.ImageRecipeBlock
import com.example.recipebook.ui.composables.recipeCreate.ImageRecipeBlockContainer
import com.example.recipebook.ui.composables.recipeCreate.ImageRecipeBlockTypes
import com.example.recipebook.ui.composables.recipeCreate.testBlockContainer
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeFromImage_RotateLeft
import com.example.recipebook.ui.theme.RecipeFromImage_RotateRight

@Composable
fun RecipeFromImageSection(
    modifier: Modifier = Modifier,
    isRecipeFromImagePopupOpen: Boolean = false,
    imageRecipeBlockContainer: ImageRecipeBlockContainer? = null,
    recipeFromImageBitmap: ImageBitmap?,
    imageOrientation: Int = 0,
    recipeFromImageTakeImage: () -> Unit,
    recipeFromImagePickImage: () -> Unit,
    recipeFromImage: () -> Unit,
    updateBlockContainer: (ImageRecipeBlockContainer?) -> Unit,
    closeRecipeFromImagePopup: () -> Unit,
    loadRecipeFromImageResult: (Boolean) -> Unit,
    updateBlockType: (ImageRecipeBlock, Int) -> Unit,
    updateBlockElementIndex: (ImageRecipeBlock, Int, Int) -> Unit,
    updateBlockIsCollapsed: (ImageRecipeBlock, Int) -> Unit,
    rotateRecipeFromImage90Right: () -> Unit,
    rotateRecipeFromImage90Left: () -> Unit
) {
    FormSection(
        modifier = modifier,
        recipeFromImageTakeImage = recipeFromImageTakeImage,
        recipeFromImagePickImage = recipeFromImagePickImage
    )

    CardDialog(
        isOpen = isRecipeFromImagePopupOpen,
        closeDialog = closeRecipeFromImagePopup,
        modifier = Modifier.fillMaxSize()
    ){
        if (imageRecipeBlockContainer == null) {
            // Select image orientation
            if (recipeFromImageBitmap != null) {
                ImageRotationSection(
                    modifier = Modifier.fillMaxSize(),
                    imageBitmap = recipeFromImageBitmap,
                    imageOrientation = imageOrientation,
                    rotateRecipeFromImage90Right = rotateRecipeFromImage90Right,
                    rotateRecipeFromImage90Left = rotateRecipeFromImage90Left,
                    recipeFromImage = recipeFromImage
                )
            }
        } else {
            // Select rows
            RowSelectionSection(
                modifier = Modifier.fillMaxSize(),
                imageRecipeBlockContainer = imageRecipeBlockContainer,
                updateBlockContainer = updateBlockContainer,
                loadRecipeFromImageResult = loadRecipeFromImageResult,
                closeRecipeFromImagePopup = closeRecipeFromImagePopup,
                updateBlockType = updateBlockType,
                updateBlockElementIndex = updateBlockElementIndex,
                updateBlockIsCollapsed = updateBlockIsCollapsed
            )
        }
    }
}

@Composable
private fun FormSection(
    modifier: Modifier = Modifier,
    recipeFromImageTakeImage: () -> Unit,
    recipeFromImagePickImage: () -> Unit
) {
    Column(modifier = modifier) {
        Button(
            onClick = recipeFromImageTakeImage
        ) {
            Text(stringResource(R.string.recipeFromImage_takeImage))
        }
        Button(
            onClick = recipeFromImagePickImage
        ) {
            Text(stringResource(R.string.recipeFromImage_pickImage))
        }
    }
}

@Composable
private fun ImageRotationSection(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    imageOrientation: Int,
    rotateRecipeFromImage90Right: () -> Unit,
    rotateRecipeFromImage90Left: () -> Unit,
    recipeFromImage: () -> Unit
) {
    Column(modifier = modifier) {
        Image(
            modifier = Modifier
                .weight(1F)
                .rotate(imageOrientation.toFloat()),
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = ContentScale.FillWidth,

            )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = rotateRecipeFromImage90Left
            ) {
                Icon(imageVector = RecipeFromImage_RotateLeft, contentDescription = "")
            }

            IconButton(
                onClick = rotateRecipeFromImage90Right
            ) {
                Icon(imageVector = RecipeFromImage_RotateRight, contentDescription = "")
            }

            Button(
                onClick = recipeFromImage
            ) {
                Text(stringResource(R.string.recipeFromImage_loadImage))
            }
        }
    }
}

@Composable
private fun RowSelectionSection(
    modifier: Modifier = Modifier,
    imageRecipeBlockContainer: ImageRecipeBlockContainer = ImageRecipeBlockContainer(),
    updateBlockContainer: (ImageRecipeBlockContainer?) -> Unit,
    loadRecipeFromImageResult: (Boolean) -> Unit,
    closeRecipeFromImagePopup: () -> Unit,
    updateBlockType: (ImageRecipeBlock, Int) -> Unit,
    updateBlockElementIndex: (ImageRecipeBlock, Int, Int) -> Unit,
    updateBlockIsCollapsed: (ImageRecipeBlock, Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        RowCategorySelectionSection(
            modifier = Modifier.fillMaxWidth(),
            imageRecipeBlockContainer = imageRecipeBlockContainer,
            updateBlockContainer = updateBlockContainer
        )

        RecipeFromImageSectionRowList(
            modifier = Modifier.weight(1F),
            imageRecipeBlockContainer = imageRecipeBlockContainer,
            updateBlockType = updateBlockType,
            updateBlockElementIndex = updateBlockElementIndex,
            updateBlockIsCollapsed = updateBlockIsCollapsed
        )

        RowCategoryConfirmSection(
            modifier = Modifier.fillMaxWidth(),
            loadRecipeFromImageResult = loadRecipeFromImageResult,
            closeRecipeFromImagePopup = closeRecipeFromImagePopup
        )
    }
}

@Composable
private fun RowCategorySelectionSection(
    modifier: Modifier = Modifier,
    imageRecipeBlockContainer: ImageRecipeBlockContainer = ImageRecipeBlockContainer(),
    updateBlockContainer: (ImageRecipeBlockContainer?) -> Unit
) {
    FlowRow(
        modifier = modifier
    ) {
        for (blockType in ImageRecipeBlockTypes.entries) {
            val isCurrentButton = blockType == imageRecipeBlockContainer.currentBlockType
            val buttonBrightness = if (isCurrentButton) {
                1.0F
            } else {
                0.5F
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = blockType.color.copy(alpha = buttonBrightness)),
                onClick = {
                    updateBlockContainer(
                        imageRecipeBlockContainer.copy(
                            currentBlockType = if (isCurrentButton) {
                                null
                            } else {
                                blockType
                            }
                        )
                    )
                }
            ) {
                Text(stringResource(blockType.displayName))
            }
        }
    }
}

@Composable
private fun RowCategoryConfirmSection(
    modifier: Modifier = Modifier,
    loadRecipeFromImageResult: (Boolean) -> Unit,
    closeRecipeFromImagePopup: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Button(
            onClick = {
                loadRecipeFromImageResult(false)
                closeRecipeFromImagePopup()
            }
        ) {
            Text(stringResource(R.string.recipeFromImage_addToRecipe))
        }

        Button(
            onClick = {
                loadRecipeFromImageResult(true)
                closeRecipeFromImagePopup()
            }
        ) {
            Text(stringResource(R.string.recipeFromImage_override))
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun RecipeFromImageSectionPreview() {
    RecipeBookTheme {
        RecipeFromImageSection(
            recipeFromImagePickImage = {},
            recipeFromImageTakeImage = {},
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            recipeFromImageBitmap = null,
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromImageSectionImageRotationPreview() {
    RecipeBookTheme {
        RecipeFromImageSection(
            recipeFromImagePickImage = {},
            recipeFromImageTakeImage = {},
            isRecipeFromImagePopupOpen = true,
            imageRecipeBlockContainer = null,
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            recipeFromImageBitmap = RecipeExamples.recipeImageBitmap,
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
        )
    }
}

@PhonePreview
@Composable
fun RecipeFromImageSectionRowSelectionPreview() {
    RecipeBookTheme {
        RecipeFromImageSection(
            isRecipeFromImagePopupOpen = true,
            imageRecipeBlockContainer = testBlockContainer,
            recipeFromImagePickImage = {},
            recipeFromImageTakeImage = {},
            recipeFromImage = {},
            updateBlockContainer = {},
            closeRecipeFromImagePopup = {},
            loadRecipeFromImageResult = {},
            updateBlockType = { _, _ -> /* Do Nothing */ },
            updateBlockElementIndex = { _, _, _ -> /* Do Nothing */ },
            updateBlockIsCollapsed = { _, _ -> /* Do Nothing */ },
            recipeFromImageBitmap = null,
            rotateRecipeFromImage90Right = {},
            rotateRecipeFromImage90Left = {}
        )
    }
}

@PhonePreview
@Composable
fun RowCategorySelectionSectionPreview() {
    RecipeBookTheme {
        RowCategorySelectionSection(
            imageRecipeBlockContainer = testBlockContainer,
            updateBlockContainer = {}
        )
    }
}

@PhonePreview
@Composable
fun RowCategoryConfirmSectionPreview() {
    RecipeBookTheme {
        RowCategoryConfirmSection(
            loadRecipeFromImageResult = {},
            closeRecipeFromImagePopup = {}
        )
    }
}

//endregion