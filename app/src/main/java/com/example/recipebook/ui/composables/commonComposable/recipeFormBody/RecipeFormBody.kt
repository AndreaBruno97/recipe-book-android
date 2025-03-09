package com.example.recipebook.ui.composables.commonComposable.recipeFormBody

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipebook.R
import com.example.recipebook.data.recipe.RecipeExamples
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.example.recipebook.ui.theme.RecipeForm_AddIngredient
import com.example.recipebook.ui.theme.RecipeForm_DeleteIngredient
import com.example.recipebook.ui.theme.RecipeForm_IngredientGoDown
import com.example.recipebook.ui.theme.RecipeForm_IngredientGoUp

@Composable
fun RecipeFormBody(
    recipeUiState: RecipeUiState,
    onRecipeValueChange: (RecipeDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeInputForm(
            recipeDetails = recipeUiState.recipeDetails,
            onValueChange = onRecipeValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = recipeUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(text = stringResource(R.string.save_button_label))
        }
    }
}

@Composable
fun RecipeInputForm(
    recipeDetails: RecipeDetails,
    modifier: Modifier = Modifier,
    onValueChange: (RecipeDetails) -> Unit = {},
    enabled: Boolean = true
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        RecipeTextInput(
            value = recipeDetails.name,
            onValueChange = { onValueChange(recipeDetails.copy(name = it)) },
            labelText = stringResource(R.string.recipe_name) + "*",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        RecipeIngredientsInput(
            recipeDetails = recipeDetails,
            onValueChange = onValueChange,
            enabled = enabled
        )

        RecipeTextInput(
            value = recipeDetails.method,
            onValueChange = { onValueChange(recipeDetails.copy(method = it)) },
            labelText = stringResource(R.string.recipe_method) + "*",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.formLegend_mandatoryFields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun RecipeIngredientsInput(
    recipeDetails: RecipeDetails,
    onValueChange: (RecipeDetails) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ){
        Text(stringResource(R.string.recipe_ingredients) + "*")

        for((index, ingredient) in recipeDetails.ingredients.withIndex()){
            val canGoUp = index > 0
            val canGoDown = index < recipeDetails.ingredients.size - 1

            IngredientInputLine(
                ingredient = ingredient,
                onNameChange = { newName ->
                    val newIngredientList = recipeDetails.ingredients.map { it.copy() }
                    newIngredientList[index].name = newName
                    onValueChange(recipeDetails.copy(ingredients = newIngredientList))
                },
                canGoUp = canGoUp,
                canGoDown = canGoDown,
                onValueChange = { newValue ->
                    val newIngredientList = recipeDetails.ingredients.map { it.copy() }
                    newIngredientList[index].value = newValue
                    onValueChange(recipeDetails.copy(ingredients = newIngredientList))
                },
                onDelete = {
                    val newIngredientList = recipeDetails.ingredients
                        .map { it.copy() }
                        .filterIndexed { curIndex, _ -> curIndex != index }
                    onValueChange(recipeDetails.copy(ingredients = newIngredientList))
                },
                onGoUp = {
                    val newIngredientList = recipeDetails.ingredients
                        .map { it.copy() }
                        .toMutableList()

                    newIngredientList.removeAt(index)
                    newIngredientList.add(index-1, ingredient)
                    onValueChange(recipeDetails.copy(ingredients = newIngredientList))
                },
                onGoDown = {
                    val newIngredientList = recipeDetails.ingredients
                        .map { it.copy() }
                        .toMutableList()

                    newIngredientList.removeAt(index)
                    newIngredientList.add(index+1, ingredient)
                    onValueChange(recipeDetails.copy(ingredients = newIngredientList))
                },
                enabled = enabled
            )
        }

        Button(
            onClick = {
                onValueChange(
                    recipeDetails.copy(
                        ingredients = recipeDetails.ingredients
                            .plus(listOf(IngredientDetails()))
                    )
                )
            }
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = RecipeForm_AddIngredient, contentDescription = "")
                Text(
                    text = stringResource(id = R.string.recipeForm_addIngredient),
                    modifier = Modifier.weight(1F)
                )
            }
        }
    }
}

@Composable
fun RecipeTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
){
    val minLines = if(singleLine) 1 else 5
    val maxLines = if(singleLine) 2 else 10

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines
    )
}

@Composable
fun IngredientInputLine(
    ingredient: IngredientDetails,
    onNameChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit,
    canGoUp: Boolean,
    canGoDown: Boolean,
    onGoUp: () -> Unit,
    onGoDown: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.fillMaxHeight()
        ){
            IconButton(
                onClick = onGoUp,
                modifier = modifier.size(dimensionResource(R.dimen.padding_medium)),
                enabled = enabled && canGoUp
            ){
                Icon(imageVector = RecipeForm_IngredientGoUp, contentDescription = "")
            }
            IconButton(
                onClick = onGoDown,
                modifier = modifier.size(dimensionResource(R.dimen.padding_medium)),
                enabled = enabled && canGoDown
            ){
                Icon(imageVector = RecipeForm_IngredientGoDown, contentDescription = "")
            }
        }
        TextField(
            value = ingredient.name,
            onValueChange = onNameChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 7F)
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
        TextField(
            value = ingredient.value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.weight(weight = 3F)
        )
        IconButton(
            onClick = onDelete,
            enabled = enabled
        ) {
            Icon(imageVector = RecipeForm_DeleteIngredient, contentDescription = "")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeFormBodyScreenPreview(){
    RecipeBookTheme {
        RecipeFormBody(
            recipeUiState = RecipeUiState(
                RecipeExamples.recipe1.toRecipeDetails()
            ),
            onRecipeValueChange = {},
            onSaveClick = {}
        )
    }
}