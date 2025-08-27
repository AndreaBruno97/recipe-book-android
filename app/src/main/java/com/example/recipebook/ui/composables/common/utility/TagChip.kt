package com.example.recipebook.ui.composables.common.utility

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.recipebook.data.objects.tag.TagDao
import com.example.recipebook.data.objects.tag.TagExamples
import com.example.recipebook.data.objects.tag.toTagDao
import com.example.recipebook.ui.preview.DefaultPreview
import com.example.recipebook.ui.theme.Chip_Close
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun TagChip(
    tag: TagDao,
    enabled: Boolean = true,
    selected: Boolean = false,
    showCloseIcon: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    InputChip(
        onClick = if (onClick != null) {
            onClick
        } else {
            {}
        },
        selected = selected,
        border = InputChipDefaults.inputChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = tag.color,
            selectedBorderColor = tag.color
        ),
        colors = InputChipDefaults.inputChipColors(
            labelColor = tag.color,
            selectedLabelColor = tag.color,
            selectedContainerColor = tag.color.copy(alpha = 0.3F)
        ),
        label = {
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = tag.name
            )
        },
        avatar = {
            val iconTag = tag.icon
            if (iconTag != null) {
                Text(iconTag)
            }
        },
        trailingIcon = {
            if (showCloseIcon) {
                Icon(
                    imageVector = Chip_Close,
                    contentDescription = "",
                    Modifier.size(InputChipDefaults.AvatarSize)
                )
            }
        },
        modifier = modifier
    )
}

//region Preview

@DefaultPreview
@Composable
private fun TagChipPreview() {
    RecipeBookTheme {
        TagChip(
            tag = TagExamples.tag1.toTagDao()
        )
    }
}

@DefaultPreview
@Composable
private fun TagChipSelectedPreview() {
    RecipeBookTheme {
        TagChip(
            tag = TagExamples.tag1.toTagDao(),
            selected = true
        )
    }
}

@DefaultPreview
@Composable
private fun TagChipLongTagPreview() {
    RecipeBookTheme {
        TagChip(
            tag = TagExamples.longTag.toTagDao(),
            modifier = Modifier.width(100.dp)
        )
    }
}

@DefaultPreview
@Composable
private fun TagChipCloseButtonPreview() {
    RecipeBookTheme {
        TagChip(
            tag = TagExamples.tag1.toTagDao(),
            showCloseIcon = true
        )
    }
}

//endregion