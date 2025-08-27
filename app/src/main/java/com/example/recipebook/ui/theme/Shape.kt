package com.example.recipebook.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp)
)

val RecipeList_SmallImage = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
val RecipeList_LargeImage = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
val RecipeList_FavoriteBackground = CircleShape