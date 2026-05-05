@file:OptIn(ExperimentalLayoutApi::class)

package com.example.recipebook.ui.composables.recipeDetails.internal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.data.objects.tag.Tag
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.composables.common.utility.TagChip
import com.example.recipebook.ui.composables.common.utility.WarningConfirmationDialog
import com.example.recipebook.ui.composables.common.utility.WarningDialogType
import com.example.recipebook.ui.composables.recipeDetails.RecipeDetailsUiState
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.preview.FoldablePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeDetails_DecreaseServingsNum
import com.example.recipebook.ui.theme.RecipeDetails_Edit
import com.example.recipebook.ui.theme.RecipeDetails_IncreaseServingsNum
import com.example.recipebook.ui.theme.RecipeDetails_ResetServingsNum
import com.example.recipebook.ui.theme.Recipe_isFavorite_Selected
import com.example.recipebook.ui.theme.Recipe_isFavorite_Unselected
import com.example.recipebook.ui.theme.Shapes
import com.example.recipebook.ui.theme.home_isFavoriteIcon
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import org.mongodb.kbson.ObjectId
import java.math.RoundingMode

@Composable
fun RecipeDetailsBody(
    screenSize: ScreenSize,
    recipeDetailsUiState: RecipeDetailsUiState,
    modifier: Modifier = Modifier,
    recipeImage: ImageBitmap? = null,
    curServingsNum: Int? = null,
    isDeletePopupOpen: Boolean = false,
    curIsFavorite: Boolean = true,
    enabled: Boolean = true,
    servingsRatio: Float? = null,
    onDelete: () -> Unit,
    closeDeletePopup: () -> Unit,
    increaseServingsNum: () -> Unit,
    decreaseServingsNum: () -> Unit,
    resetServingsNum: () -> Unit,
    toggleIsFavorite: () -> Unit,
    navigateToEditRecipe: (ObjectId) -> Unit
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeDetails(
            screenSize = screenSize,
            recipe = recipeDetailsUiState.recipe,
            modifier = Modifier.fillMaxWidth(),
            recipeImage = recipeImage,
            curServingsNum = curServingsNum,
            servingsRatio = servingsRatio,
            curIsFavorite = curIsFavorite,
            enabled = enabled,
            increaseServingsNum = increaseServingsNum,
            decreaseServingsNum = decreaseServingsNum,
            resetServingsNum = resetServingsNum,
            toggleIsFavorite = toggleIsFavorite,
            navigateToEditRecipe = navigateToEditRecipe
        )

        /*
        WarningConfirmationDialog(
            isPopupOpen = isDeletePopupOpen,
            text = stringResource(R.string.recipeForm_deletePopupText),
            warningType = WarningDialogType.DELETE,
            onWarningConfirm = {
                closeDeletePopup()
                onDelete()
            },
            onWarningCancel = closeDeletePopup,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        )
        */
    }
}

@Composable
private fun RecipeDetails(
    screenSize: ScreenSize,
    recipe: Recipe,
    modifier: Modifier = Modifier,
    recipeImage: ImageBitmap? = null,
    curServingsNum: Int? = null,
    servingsRatio: Float? = null,
    curIsFavorite: Boolean = false,
    enabled: Boolean = true,
    increaseServingsNum: () -> Unit,
    decreaseServingsNum: () -> Unit,
    resetServingsNum: () -> Unit,
    toggleIsFavorite: () -> Unit,
    navigateToEditRecipe: (ObjectId) -> Unit
) {
    if(screenSize == ScreenSize.SMALL){
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            RecipeTitleSection(
                recipe = recipe,
                enabled = enabled,
                curIsFavorite = curIsFavorite,
                toggleIsFavorite = toggleIsFavorite,
                navigateToEditRecipe = navigateToEditRecipe
            )

            RecipeImageSection(
                recipeImage = recipeImage,
                screenSize = screenSize
            )

            RecipeTagsSection(
                tagList = recipe.tagList
            )

            RecipeDetailsSection(
                recipe = recipe,
                screenSize = screenSize,
                curServingsNum = curServingsNum,
                enabled = enabled,
                increaseServingsNum = increaseServingsNum,
                decreaseServingsNum = decreaseServingsNum,
                resetServingsNum = resetServingsNum
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            RecipeIngredientsSection(
                ingredientGroupList = recipe.ingredientGroupList,
                servingsRatio = servingsRatio
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            RecipeMethodListSection(
                methodList = recipe.methodList
            )
        }
    }
    else{
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            RecipeTitleSection(
                recipe = recipe,
                enabled = enabled,
                curIsFavorite = curIsFavorite,
                toggleIsFavorite = toggleIsFavorite,
                navigateToEditRecipe = navigateToEditRecipe
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ){
                Column(
                    modifier = Modifier
                        .weight(0.3F),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
                ){
                    RecipeImageSection(
                        recipeImage = recipeImage,
                        screenSize = screenSize
                    )

                    RecipeIngredientsSection(
                        ingredientGroupList = recipe.ingredientGroupList,
                        servingsRatio = servingsRatio
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.7F),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ){
                    RecipeDetailsSection(
                        recipe = recipe,
                        screenSize = screenSize,
                        curServingsNum = curServingsNum,
                        enabled = enabled,
                        increaseServingsNum = increaseServingsNum,
                        decreaseServingsNum = decreaseServingsNum,
                        resetServingsNum = resetServingsNum
                    )

                    RecipeTagsSection(
                        tagList = recipe.tagList
                    )

                    RecipeMethodListSection(
                        methodList = recipe.methodList
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeImageSection(
    modifier: Modifier = Modifier,
    recipeImage: ImageBitmap? = null,
    screenSize: ScreenSize
) {

    var imageModifier = modifier
        .clip(Shapes.small)
        //.aspectRatio(16F/9F)
        //.height(dimensionResource(R.dimen.recipe_image_size_small))

    imageModifier = when(screenSize){
        ScreenSize.SMALL -> {
            imageModifier.height(dimensionResource(R.dimen.recipe_image_size_small))
        }
        ScreenSize.MEDIUM -> {
            imageModifier.aspectRatio(4F/3F)
        }
        ScreenSize.LARGE -> {
            imageModifier.aspectRatio(16F/9F)
        }
    }

    if (recipeImage != null) {
        Image(
            bitmap = recipeImage,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    }
    else{
        Image(
            painter = painterResource(id = R.drawable.placeholder_recipe_image_720p),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    }
}

@Composable
private fun RecipeTagsSection(
    modifier: Modifier = Modifier,
    tagList: RealmList<Tag>
) {
    FlowRow(
        overflow = FlowRowOverflow.Clip,
        horizontalArrangement = Arrangement
            .spacedBy(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .padding(0.dp)
            .fillMaxWidth()
    ) {
        for (tag in tagList) {
            TagChip(tag = tag.toTagDao())
        }
    }
}

@Composable
private fun RecipeDetailsSection(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    screenSize: ScreenSize,
    curServingsNum: Int? = null,
    enabled: Boolean = true,
    increaseServingsNum: () -> Unit,
    decreaseServingsNum: () -> Unit,
    resetServingsNum: () -> Unit
){
    val maxItemsInRow = when (screenSize) {
        ScreenSize.SMALL -> 1
        ScreenSize.MEDIUM -> 2
        ScreenSize.LARGE -> 3
    }
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(),
        border = CardDefaults.outlinedCardBorder()
    ) {

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            maxItemsInEachRow = maxItemsInRow
        ) {
            Row(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    stringResource(R.string.recipe_prepTimeMinutes) + ": ",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    if (recipe.prepTimeMinutes == null)
                        "-" else
                        recipe.prepTimeMinutes.toString() + " min",
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    stringResource(R.string.recipe_cookTimeMinutes) + ": ",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    if (recipe.cookTimeMinutes == null)
                        "-" else
                        recipe.cookTimeMinutes.toString() + " min",
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    stringResource(R.string.recipe_servingsNum) + ": ",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedIconButton(
                    onClick = decreaseServingsNum,
                    enabled = enabled && curServingsNum != null && curServingsNum > 1,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                ) {
                    Icon(imageVector = RecipeDetails_DecreaseServingsNum(), contentDescription = "")
                }

                Text(
                    curServingsNum?.toString() ?: "-",
                    fontWeight = FontWeight.Bold
                )

                OutlinedIconButton(
                    onClick = increaseServingsNum,
                    enabled = enabled && curServingsNum != null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                ) {
                    Icon(imageVector = RecipeDetails_IncreaseServingsNum, contentDescription = "")
                }
                IconButton(
                    onClick = resetServingsNum,
                    enabled = enabled && curServingsNum != null && curServingsNum != recipe.servingsNum,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                ) {
                    Icon(imageVector = RecipeDetails_ResetServingsNum, contentDescription = "")
                }
            }
        }
    }
}

@Composable
private fun RecipeTitleSection(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    enabled: Boolean = true,
    curIsFavorite: Boolean = false,
    toggleIsFavorite: () -> Unit,
    navigateToEditRecipe: (ObjectId) -> Unit,
){
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier
    ){
        Text(
            recipe.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1F)
        )

        IconButton(
            onClick = { navigateToEditRecipe(recipe._id) },
            enabled = enabled,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
        ) {
            Icon(
                imageVector = RecipeDetails_Edit,
                contentDescription = ""
            )
        }

        IconButton(
            onClick = toggleIsFavorite,
            enabled = enabled,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
        ) {
            if(curIsFavorite){
                Icon(
                    imageVector = Recipe_isFavorite_Selected,
                    tint = home_isFavoriteIcon,
                    contentDescription = ""
                )
            }
            else{
                Icon(
                    imageVector = Recipe_isFavorite_Unselected(),
                    contentDescription = ""
                )
            }
        }
    }

}

@Composable
private fun RecipeIngredientsSection(
    modifier: Modifier = Modifier,
    ingredientGroupList: RealmList<IngredientGroup>,
    servingsRatio: Float? = null
){
    Column(
        modifier = modifier
    ){
        Text(
            stringResource(R.string.recipe_ingredients),
            style = MaterialTheme.typography.titleMedium
        )

        for ((index, ingredientGroup) in ingredientGroupList.withIndex()) {
            val title = ingredientGroup.title

            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleSmall)
            }

            for (ingredient in ingredientGroup.ingredientList) {
                var quantityString = ""
                var quantity = ingredient.quantity

                if (quantity != null) {
                    if (servingsRatio != null) {
                        quantity *= servingsRatio
                    }

                    val formattedQuantity = quantity
                        .toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                        .toPlainString()

                    quantityString = "${formattedQuantity} "
                }

                Text("${ingredient.name}: ${quantityString}${ingredient.value}")
            }

            if (index < ingredientGroupList.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RecipeMethodListSection(
    modifier: Modifier = Modifier,
    methodList: RealmList<String>
){
    Column(
        modifier = modifier
    ){
        Text(stringResource(R.string.recipe_method), style = MaterialTheme.typography.titleMedium)

        for ((index, method) in methodList.withIndex()) {
            Text(method)
            if (index < methodList.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

//region Preview

@DefaultPreview
@Composable
fun RecipeDetailsBodyPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.SMALL,
            RecipeDetailsUiState(RecipeExamples.recipe1),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = true,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeDetailsBodyNoFavoritePreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.SMALL,
            RecipeDetailsUiState(RecipeExamples.recipe2),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = false,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeDetailsBodyEmptyFieldsPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.SMALL,
            RecipeDetailsUiState(RecipeExamples.recipe1.toRecipeDao().toRecipe().apply {
                servingsNum = null
                prepTimeMinutes = null
                cookTimeMinutes = null
                isFavorite = false
                tagList = TagExamples.tagList.plus(TagExamples.tagList).toRealmList()
            }),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@DefaultPreview
@Composable
fun IngredientQuantityFormattingPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.SMALL,
            RecipeDetailsUiState(RecipeExamples.recipe1.toRecipeDao().toRecipe().apply {
                ingredientGroupList = realmListOf(
                    IngredientGroup(
                        "Test decimali",
                        ingredientList = realmListOf(
                            Ingredient("a", 1F, "a"),
                            Ingredient("a", 12F, "a"),
                            Ingredient("a", 123F, "a"),
                            Ingredient("a", 1.016F, "a"),
                            Ingredient("a", 320F, "a"),
                            Ingredient("a", 21.010101F, "a"),
                            Ingredient("a", 1231.010000F, "a"),
                            Ingredient("a", 10.001002F, "a")
                        )
                    )
                )
            }),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = 4,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@FoldablePreview
@Composable
fun RecipeDetailsBodyFoldablePreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.MEDIUM,
            RecipeDetailsUiState(RecipeExamples.recipe1),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = true,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@TabletPreview
@Composable
fun RecipeDetailsBodyTabletPreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.LARGE,
            RecipeDetailsUiState(RecipeExamples.recipe1),
            recipeImage = RecipeExamples.recipeImageBitmap,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = true,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@DefaultPreview
@Composable
fun RecipeDetailsBodyNoImagePreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.SMALL,
            RecipeDetailsUiState(RecipeExamples.recipe2),
            recipeImage = null,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = false,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@FoldablePreview
@Composable
fun RecipeDetailsBodyFoldableNoImagePreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.MEDIUM,
            RecipeDetailsUiState(RecipeExamples.recipe1),
            recipeImage = null,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = true,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

@TabletPreview
@Composable
fun RecipeDetailsBodyTabletNoImagePreview() {
    RecipeBookTheme {
        RecipeDetailsBody(
            screenSize = ScreenSize.LARGE,
            RecipeDetailsUiState(RecipeExamples.recipe1),
            recipeImage = null,
            curServingsNum = RecipeExamples.recipe1.servingsNum,
            curIsFavorite = true,
            onDelete = {},
            closeDeletePopup = {},
            increaseServingsNum = {},
            decreaseServingsNum = {},
            resetServingsNum = {},
            toggleIsFavorite = {},
            navigateToEditRecipe = {}
        )
    }
}

//endregion