package com.example.recipebook.ui.composables.home.internal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeExamples
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.preview.FoldablePreview
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme
import org.mongodb.kbson.ObjectId


@Composable
fun HomeBody(
    recipeList: List<Recipe>,
    screenSize: ScreenSize,
    onRecipeClick: (ObjectId) -> Unit,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.no_padding))
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val columnNum = when (screenSize) {
            ScreenSize.SMALL -> 1
            ScreenSize.MEDIUM -> 2
            ScreenSize.LARGE -> 3
        }

        if (recipeList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_recipes_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            RecipeList(
                recipeList = recipeList,
                columnNum = columnNum,
                onRecipeClick = { onRecipeClick(it._id) },
                loadRecipeImage = loadRecipeImage,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun RecipeList(
    recipeList: List<Recipe>,
    columnNum: Int,
    onRecipeClick: (Recipe) -> Unit,
    loadRecipeImage: (ObjectId, Context) -> ImageBitmap?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnNum),
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(recipeList) { recipe ->
            RecipeItem(
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
private fun RecipeItem(
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
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun HomeBodyPhonePreview() {
    RecipeBookTheme {
        HomeBody(
            recipeList = RecipeExamples.recipeList,
            screenSize = ScreenSize.SMALL,
            onRecipeClick = {},
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
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
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
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
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
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
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

@DefaultPreview
@Composable
fun RecipeItemPreview() {
    RecipeBookTheme {
        RecipeItem(
            recipe = RecipeExamples.recipe1,
            loadRecipeImage = { _, _ -> RecipeExamples.recipeImageBitmap }
        )
    }
}

//endregion