package com.example.recipebook.ui.composables.home.internal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.Home_RecipeIsFavorite
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.home_isFavoriteIcon
import org.mongodb.kbson.ObjectId


@Composable
fun HomeRecipeList(
    recipeList: List<Recipe>,
    columnNum: Int,
    onRecipeClick: (Recipe) -> Unit,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding)),
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnNum),
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(recipeList) { recipe ->
            HomeRecipeItem(
                recipe = recipe,
                loadRecipeImage = loadRecipeImage,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onRecipeClick(recipe) }
            )
        }
    }
}

@Composable
private fun HomeRecipeItem(
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            if (recipeImage != null) {
                Image(
                    bitmap = recipeImage,
                    contentDescription = "",
                    //contentScale = ContentScale.FillHeight,
                    modifier = Modifier.width(100.dp)
                )
            }

            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleLarge
            )
            if (recipe.isFavorite) {
                Icon(
                    imageVector = Home_RecipeIsFavorite,
                    contentDescription = "",
                    tint = home_isFavoriteIcon
                )
            }
        }
    }
}

//region Preview

@DefaultPreview
@Composable
fun RecipeListPreview() {
    RecipeBookTheme {
        HomeRecipeList(
            recipeList = RecipeExamples.recipeList,
            columnNum = 1,
            onRecipeClick = {},
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemPreview() {
    RecipeBookTheme {
        HomeRecipeItem(
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

//endregion