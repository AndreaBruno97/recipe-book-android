package com.example.recipebook.ui.composables.common.utility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.recipebook.R
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.theme.CardDialog_Close
import com.example.recipebook.ui.theme.RecipeBookTheme

@Composable
fun CardDialog(
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
    closeDialog: () -> Unit,
    content: @Composable() () -> Unit
) {
    if(isOpen){
        Dialog(
            onDismissRequest = closeDialog
        ){
            Card(
                modifier = modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .zIndex(800F)
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                ){
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = closeDialog
                        ) {
                            Icon(
                                imageVector = CardDialog_Close,
                                contentDescription = ""
                            )
                        }
                    }
                    content()
                }
            }
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun CardDialogPreview(){
    RecipeBookTheme{
        CardDialog(
            isOpen = true,
            closeDialog = {}
        ) {
            Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce tempor volutpat arcu eu sollicitudin.")
        }
    }
}

@PhonePreview
@Composable
fun CardDialogClosedPreview(){
    RecipeBookTheme{
        CardDialog(
            isOpen = false,
            closeDialog = {}
        ) {
            Text("Content - Not visible")
        }
    }
}


//endregion