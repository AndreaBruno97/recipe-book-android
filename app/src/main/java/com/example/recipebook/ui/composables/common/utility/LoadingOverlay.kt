package com.example.recipebook.ui.composables.common.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.zIndex
import com.example.recipebook.R
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun LoadingOverlay(
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.5F))
                .zIndex(999F)
                .clickable(
                    /* Remove ripple animation on click */
                    interactionSource = null,
                    indication = null
                ) {
                    /* Prevent the user from clicking on elements behind the box*/
                },
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(dimensionResource(R.dimen.progress_indicator_size)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

//region Preview

@DefaultPreview
@Composable
private fun LoadingOverlayPreview() {
    RecipeBookTheme {
        LoadingOverlay(true)
    }
}

//endregion