package com.example.recipebook.ui.composables.recipeDetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.loadRecipeImage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mongodb.kbson.ObjectId
import java.io.File

class RecipeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    private val recipeIdString: String =
        checkNotNull(savedStateHandle[RecipeDetailsDestination.recipeIdArg])
    private val recipeId: ObjectId = ObjectId(recipeIdString)

    val uiState: StateFlow<RecipeDetailsUiState> =
        recipeRepository.getRecipeById(recipeId)
            .filterNotNull()
            .map {
                curServingsNum = it.servingsNum

                RecipeDetailsUiState(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RecipeDetailsUiState()
            )

    var curServingsNum: Int? by mutableStateOf(null)

    val servingsRatio: Float?
        get() {
            val baseServingsNum = uiState.value.recipe.servingsNum
            val curServingsNum = curServingsNum

            return if (
                baseServingsNum != null &&
                curServingsNum != null &&
                baseServingsNum != 0
            ) {
                curServingsNum.toFloat() / baseServingsNum
            } else {
                null
            }
        }

    var isDeletePopupOpen by mutableStateOf(false)
        private set

    var recipeImage: ImageBitmap? by mutableStateOf(null)

    fun openDeletePopup() {
        isDeletePopupOpen = true
    }

    fun closeDeletePopup() {
        isDeletePopupOpen = false
    }

    fun increaseServingsNum() {
        val curServingsNumTmp = curServingsNum

        if (curServingsNumTmp != null) {
            curServingsNum = curServingsNumTmp + 1
        }
    }

    fun decreaseServingsNum() {
        val curServingsNumTmp = curServingsNum

        if (curServingsNumTmp != null && curServingsNumTmp > 1) {
            curServingsNum = curServingsNumTmp - 1
        }
    }

    fun resetServingsNum() {
        curServingsNum = uiState.value.recipe.servingsNum
    }

    suspend fun deleteRecipe(context: Context) {
        recipeRepository.removeRecipe(uiState.value.recipe)

        val recipeFolderPath = getRecipeFolderPath(recipeId)
        val folderFile = File(context.filesDir, recipeFolderPath)

        if (folderFile.isDirectory) {
            folderFile.delete()
        }
    }

    fun loadRecipeImage(context: Context) {
        if (recipeImage == null) {
            recipeImage = loadRecipeImage(recipeId, context)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RecipeDetailsUiState(
    val recipe: Recipe = Recipe()
)