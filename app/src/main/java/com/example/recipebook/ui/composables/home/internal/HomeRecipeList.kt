package com.example.recipebook.ui.composables.home.internal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.Home_RecipeIsFavorite
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeList_FavoriteBackground
import com.example.recipebook.ui.theme.RecipeList_LargeImage
import com.example.recipebook.ui.theme.RecipeList_SmallImage
import com.example.recipebook.ui.theme.home_isFavoriteIcon
import org.mongodb.kbson.ObjectId


@Composable
fun HomeRecipeList(
    screenSize: ScreenSize,
    recipeList: List<Recipe>,
    columnNum: Int,
    onRecipeClick: (Recipe) -> Unit,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnNum),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
    ) {
        items(recipeList) { recipe ->
            HomeRecipeItem(
                recipe = recipe,
                loadRecipeImage = loadRecipeImage,
                screenSize = screenSize,
                modifier = Modifier
                    .clickable { onRecipeClick(recipe) }
            )
        }
    }
}

@Composable
private fun HomeRecipeItem(
    screenSize: ScreenSize,
    recipe: Recipe,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recipeImage = loadRecipeImage(recipe._id, context)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation))
    ) {
        Box {
            if (screenSize == ScreenSize.SMALL) {
                // Small Size

                val imageModifier = Modifier
                    .size(dimensionResource(id = R.dimen.card_image_size_small))
                    .clip(RecipeList_SmallImage)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {

                    if (recipeImage != null) {
                        Image(
                            bitmap = recipeImage,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = imageModifier
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_recipe_image_small),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = imageModifier
                        )
                    }

                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                    )
                }
            } else {
                // Medium and Large Size

                val imageModifier = Modifier
                    .height(
                        dimensionResource(
                            id =
                            if (screenSize == ScreenSize.MEDIUM) {
                                R.dimen.card_image_height_medium
                            } else {
                                R.dimen.card_image_height_large
                            }
                        )
                    )
                    .fillMaxWidth()
                    .clip(RecipeList_LargeImage)

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (recipeImage != null) {
                        Image(
                            bitmap = recipeImage,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = imageModifier
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_recipe_image_large),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = imageModifier
                        )
                    }

                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                    )
                }
            }

            if (recipe.isFavorite) {
                Box(
                    modifier = Modifier
                        .clip(RecipeList_FavoriteBackground)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Home_RecipeIsFavorite,
                        contentDescription = "",
                        tint = home_isFavoriteIcon,
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.recipe_card_favorite_icon_size))
                    )
                }
            }
        }
    }
}

//region Preview

private fun loadRecipeImage(objectId: ObjectId): ImageBitmap? {
    return if (objectId == RecipeExamples.recipe2._id) {
        null
    } else {
        RecipeExamples.recipeImageBitmap
    }
}

@DefaultPreview
@Composable
fun RecipeListPreview() {
    RecipeBookTheme {
        HomeRecipeList(
            screenSize = ScreenSize.SMALL,
            recipeList = RecipeExamples.recipeList,
            columnNum = 1,
            onRecipeClick = {},
            loadRecipeImage = { objectId, _ -> loadRecipeImage(objectId) }
        )
    }
}


@DefaultPreview
@Composable
fun RecipeListTwoColumnsPreview() {
    RecipeBookTheme {
        HomeRecipeList(
            screenSize = ScreenSize.MEDIUM,
            recipeList = RecipeExamples.longRecipeList,
            columnNum = 2,
            onRecipeClick = {},
            loadRecipeImage = { objectId, _ -> loadRecipeImage(objectId) }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemPreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            screenSize = ScreenSize.SMALL,
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemNoImageFavoritePreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            screenSize = ScreenSize.SMALL,
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> null }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemMediumImagePreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            screenSize = ScreenSize.MEDIUM,
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemMediumImageNoImagePreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            screenSize = ScreenSize.MEDIUM,
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> null }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemLargeImagePreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            screenSize = ScreenSize.LARGE,
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemLargeImageNoImagePreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            screenSize = ScreenSize.LARGE,
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> null }
        )
    }
}

//endregion