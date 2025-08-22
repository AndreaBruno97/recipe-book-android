package com.example.recipebook.ui.composables.common.utility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.recipebook.R
import com.example.recipebook.ui.composables.recipeCreate.internal.RecipeFromWebsiteSection
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.CollapsableSection_Collapse
import com.example.recipebook.ui.theme.CollapsableSection_Expand
import com.example.recipebook.ui.theme.RecipeBookTheme
import com.google.android.datatransport.runtime.dagger.Component

@Composable
fun CollapsableSection(
    isCollapsed: Boolean = true,
    title: String,
    modifier: Modifier = Modifier,
    collapseSection: () -> Unit,
    expandSection: () -> Unit,
    content: @Composable () -> Unit
) {
    val (toggleSectionIcon, toggleSectionAction) =
        if (isCollapsed) {
            Pair(
                CollapsableSection_Expand,
                expandSection
            )
        } else {
            Pair(
                CollapsableSection_Collapse,
                collapseSection
            )
        }

    Column(modifier = modifier){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1F)
            )
            IconButton(
                onClick = toggleSectionAction
            ) {
                Icon(
                    imageVector = toggleSectionIcon,
                    contentDescription = ""
                )
            }
        }

        if(isCollapsed == false){
            content()
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun CollapsableSectionCollapsedPreview() {
    RecipeBookTheme {
        CollapsableSection(
            isCollapsed = true,
            title = "Section Title - Collapsed",
            collapseSection = {},
            expandSection = {}
        ){
            Text("Content")
        }
    }
}

@PhonePreview
@Composable
fun CollapsableSectionExpandedPreview() {
    RecipeBookTheme {
        CollapsableSection(
            isCollapsed = false,
            title = "Section Title - Expanded",
            collapseSection = {},
            expandSection = {}
        ){
            Text("Content")
        }
    }
}

//endregion