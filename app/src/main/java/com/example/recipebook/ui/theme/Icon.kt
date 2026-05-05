package com.example.recipebook.ui.theme

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.recipebook.R

val TopAppBar_Back = Icons.AutoMirrored.Filled.ArrowBack
val BottomAppBar_TagList = Icons.AutoMirrored.Filled.List
val BottomAppBar_BackupManager = Icons.Default.Lock
val BottomAppBar_CreateRecipe = Icons.Default.Add
val HomeFab_Filter = Icons.Default.Search

val Home_EmptyList = Icons.Default.Edit
val Home_RecipeFilter_AddIngredient = Icons.Default.Search
val Home_RecipeIsFavorite = Icons.Default.Star

val RecipeDetails_IncreaseServingsNum = Icons.Default.Add
val RecipeDetails_ResetServingsNum = Icons.Default.Refresh
val RecipeDetails_Edit = Icons.Default.Edit

val RecipeForm_AddIngredient = Icons.Default.Add
val RecipeForm_AddTag = Icons.Default.Add
val RecipeForm_AddMethod = Icons.Default.Add

val TagList_FabAddTag = Icons.Default.Add
val TagList_EmptyList = Icons.Default.Edit

val SortableList_ItemGoUp = Icons.Default.KeyboardArrowUp
val SortableList_ItemGoDown = Icons.Default.KeyboardArrowDown
val SortableList_AddItem = Icons.Default.Add

val RecipeFromImage_ShowRow = Icons.Default.KeyboardArrowDown
val RecipeFromImage_HideRow = Icons.Default.KeyboardArrowUp
val RecipeFromImage_RotateLeft = Icons.AutoMirrored.Filled.KeyboardArrowLeft
val RecipeFromImage_RotateRight = Icons.AutoMirrored.Filled.KeyboardArrowRight

val TextInput_Error = Icons.Default.Warning

val ClearableItem_Clear = Icons.Default.Close

val CollapsableSection_Collapse = Icons.Default.KeyboardArrowUp
val CollapsableSection_Expand = Icons.Default.KeyboardArrowDown

val CardDialog_Close = Icons.Default.Close

val Chip_Close = Icons.Default.Close

val BackupManager_Warning = Icons.Default.Warning


val Recipe_isFavorite_Selected = Icons.Default.Star

// Icons loaded as drawables
@Composable
fun Recipe_isFavorite_Unselected(): ImageVector{
    return ImageVector.vectorResource(id = R.drawable.baseline_star_outline_24)
}
@Composable
fun RecipeDetails_DecreaseServingsNum(): ImageVector{
    return ImageVector.vectorResource(id = R.drawable.remove_24dp_1f1f1f_fill0_wght400_grad0_opsz24)
}