@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.recipebook.ui.composables.recipeCreate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.R
import com.example.recipebook.data.objects.ingredient.IngredientDao
import com.example.recipebook.data.objects.ingredient.IngredientGroupTitleDao
import com.example.recipebook.data.objects.ingredient.IngredientItemDao
import com.example.recipebook.data.objects.method.MethodDao
import com.example.recipebook.data.objects.recipe.RecipeDao
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.recipe.toRecipeDao
import com.example.recipebook.network.Network
import com.example.recipebook.network.SupportedWebsites
import com.example.recipebook.ui.composables.common.recipeFormBody.RecipeUiState
import com.example.recipebook.ui.composables.common.utility.getRecipeFolderPath
import com.example.recipebook.ui.composables.common.utility.saveRecipeImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.min
import kotlin.math.sqrt

enum class RecipeWebsiteUrlErrors {
    INVALID_URL, WEBSITE_NOT_MANAGED, LOADING_ERROR
}

enum class ImageRecipeBlockTypes(val color: Color, @StringRes val displayName: Int) {
    TITLE(Color.Red, R.string.recipe_name),
    INGREDIENT_TITlE(Color.Green, R.string.recipe_ingredientTitle),
    INGREDIENT(Color.Yellow, R.string.recipe_ingredient),
    METHOD(Color.Blue, R.string.recipe_method)
}

enum class ImageDestination {
    MAIN_RECIPE_IMAGE, IMAGE_TO_RECIPE_CONVERTER
}

data class ImageRecipeBlock(
    val blockType: ImageRecipeBlockTypes? = null,
    val isCollapsed: Boolean = true,
    val text: String = "",
    val elementList: List<String> = listOf(),
    val quantityElementIndex: Int? = null
)

data class ImageRecipeBlockContainer(
    val currentBlockType: ImageRecipeBlockTypes? = null,
    val imageRecipeBlockList: List<ImageRecipeBlock> = listOf()
)


val testBlockContainer = ImageRecipeBlockContainer(
    imageRecipeBlockList = listOf(
        ImageRecipeBlock(
            blockType = null,
            text = "Test",
            elementList = listOf("Test")
        ),
        ImageRecipeBlock(
            blockType = ImageRecipeBlockTypes.TITLE,
            isCollapsed = true,
            text = "Pasta al sugo\nSeconda riga",
            elementList = listOf("Pasta", "al", "sugo", "seconda", "riga")
        ),
        ImageRecipeBlock(
            blockType = ImageRecipeBlockTypes.INGREDIENT_TITlE,
            text = "Titolo ingredienti",
            elementList = listOf("Titolo", "ingredienti")
        ),
        ImageRecipeBlock(
            blockType = ImageRecipeBlockTypes.INGREDIENT,
            text = "Pasta 200 g",
            elementList = listOf("Pasta", "200", "g"),
            quantityElementIndex = 1
        ),
        ImageRecipeBlock(
            blockType = ImageRecipeBlockTypes.INGREDIENT,
            text = "Ingrediente molto lungo che andrà oltre una singola riga",
            elementList = listOf(
                "Ingrediente",
                "molto",
                "lungo",
                "che",
                "andrà",
                "oltre",
                "una",
                "singola",
                "riga"
            ),
            quantityElementIndex = 0
        ),
        ImageRecipeBlock(
            blockType = ImageRecipeBlockTypes.METHOD,
            text = "Cuoci la pasta e il sugo",
            elementList = listOf("Cuoci", "la", "pasta", "e", "il", "sugo")
        ),

        )
)

class RecipeCreateViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    //region Recipe Creation

    var recipeUiState by mutableStateOf(RecipeUiState())
        private set

    var currentImageDestination: ImageDestination? by mutableStateOf(null)
        private set

    fun updateUiState(recipeDao: RecipeDao) {
        recipeUiState = recipeUiState.copy(recipeDao = recipeDao)
    }

    fun updateCurrentImageDestination(newImageDestination: ImageDestination?) {
        currentImageDestination = newImageDestination
    }

    fun updateUiStateImage(recipeImage: ImageBitmap?, recipeImageTmpPath: String?) {
        recipeUiState =
            recipeUiState.copy(recipeImage = recipeImage, recipeImageTmpPath = recipeImageTmpPath)
    }

    suspend fun saveRecipe(context: Context): ObjectId? {
        if (recipeUiState.recipeDao.validateInput()) {
            val recipeId = recipeRepository.addRecipe(recipeUiState.recipeDao.toRecipe())

            val recipeFolderPath = getRecipeFolderPath(recipeId)

            saveRecipeImage(
                recipeFolderPath,
                recipeUiState.recipeImageTmpPath,
                context
            )

            return recipeId
        }

        updateUiState(recipeUiState.recipeDao.getInputValidationCopy())

        return null
    }

    //endregion

    //region Recipe From Website

    var recipeWebsiteUrl by mutableStateOf("")
        private set
    var validateRecipeWebsiteUrl: RecipeWebsiteUrlErrors? by mutableStateOf(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isRecipeFromWebsiteSectionVisible by mutableStateOf(false)
        private set

    fun showRecipeFromWebsiteSection() {
        isRecipeFromWebsiteSectionVisible = true
    }

    fun hideRecipeFromWebsiteSection() {
        isRecipeFromWebsiteSectionVisible = false
    }

    fun updateRecipeWebsiteUrl(url: String) {
        recipeWebsiteUrl = url
        validateRecipeWebsiteUrl = null
    }

    fun loadRecipeFromWebsite(compositionContext: Context) {

        try {
            val parsedRecipeWebsiteUrl = URL(recipeWebsiteUrl)

            if (parsedRecipeWebsiteUrl.host !in SupportedWebsites.entries.map { it.host }) {
                validateRecipeWebsiteUrl = RecipeWebsiteUrlErrors.WEBSITE_NOT_MANAGED
                return
            }
        } catch (e: MalformedURLException) {
            validateRecipeWebsiteUrl = RecipeWebsiteUrlErrors.INVALID_URL
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true

            val recipeResult = Network.getPage(recipeWebsiteUrl, compositionContext.cacheDir)

            if (recipeResult.success) {
                if (recipeResult.recipe != null) {
                    updateUiState(recipeResult.recipe.toRecipeDao())
                }

                if (recipeResult.imageBitmap != null && recipeResult.imagePath != null) {
                    updateUiStateImage(
                        recipeResult.imageBitmap,
                        recipeResult.imagePath
                    )
                }

                validateRecipeWebsiteUrl = null
            } else {
                validateRecipeWebsiteUrl = RecipeWebsiteUrlErrors.LOADING_ERROR
            }

            isLoading = false
        }
    }

    //endregion

    //region Recipe From Image

    private val imageSizeThreshold = 1024 * 1024 * 5 // 5 MB

    var recipeFromImageBitmap: ImageBitmap? by mutableStateOf(null)
        private set

    var recipeFromImagePath: String? by mutableStateOf(null)
        private set

    var recipeFromImageOrientation: Int by mutableStateOf(0)
        private set

    var isRecipeFromImagePopupOpen by mutableStateOf(false)
        private set

    var imageRecipeBlockContainer: ImageRecipeBlockContainer? by mutableStateOf(null)
        private set

    fun updateRecipeFromImageBitmap(newImage: ImageBitmap?, newImagePath: String?) {
        recipeFromImageBitmap = newImage
        recipeFromImagePath = newImagePath
        isRecipeFromImagePopupOpen = true
    }

    fun rotateRecipeFromImageBitmap(degrees: Int, clockwise: Boolean) {
        val degreesToAdd = if (clockwise) {
            degrees
        } else {
            -degrees
        }
        val newDegrees = recipeFromImageOrientation + degreesToAdd
        recipeFromImageOrientation = newDegrees.mod(360)
    }

    fun rotateRecipeFromImage90Right() {
        rotateRecipeFromImageBitmap(degrees = 90, clockwise = true)
    }

    fun rotateRecipeFromImage90Left() {
        rotateRecipeFromImageBitmap(degrees = 90, clockwise = false)
    }

    fun updateBlockContainer(blockContainer: ImageRecipeBlockContainer?) {
        imageRecipeBlockContainer = blockContainer?.copy()
    }

    fun resetRecipeFromImage() {
        recipeFromImageBitmap = null
        recipeFromImagePath = null
        recipeFromImageOrientation = 0
        imageRecipeBlockContainer = null
    }

    fun updateBlock(blockToUpdate: ImageRecipeBlock, indexToUpdate: Int) {
        val blockContainer = imageRecipeBlockContainer

        if (blockContainer != null) {
            updateBlockContainer(
                blockContainer.copy(
                    imageRecipeBlockList = blockContainer
                        .imageRecipeBlockList
                        .mapIndexed { copyIndex, copyBlock ->
                            if (indexToUpdate == copyIndex) {
                                blockToUpdate
                            } else {
                                copyBlock
                            }
                        }
                )
            )
        }
    }

    fun updateBlockType(blockToUpdate: ImageRecipeBlock, indexToUpdate: Int) {
        val blockContainer = imageRecipeBlockContainer

        if (blockContainer != null) {
            updateBlock(
                blockToUpdate.copy(
                    blockType = if (blockToUpdate.blockType == blockContainer.currentBlockType) {
                        null
                    } else {
                        blockContainer.currentBlockType
                    }
                ),
                indexToUpdate
            )
        }
    }

    fun updateBlockElementIndex(
        blockToUpdate: ImageRecipeBlock,
        elementIndex: Int,
        indexToUpdate: Int
    ) {
        val blockContainer = imageRecipeBlockContainer

        if (blockContainer != null) {
            updateBlock(
                blockToUpdate.copy(
                    quantityElementIndex = if (blockToUpdate.quantityElementIndex == elementIndex) {
                        null
                    } else {
                        elementIndex
                    }
                ),
                indexToUpdate
            )
        }
    }

    fun updateBlockIsCollapsed(blockToUpdate: ImageRecipeBlock, indexToUpdate: Int) {
        val blockContainer = imageRecipeBlockContainer

        if (blockContainer != null) {
            updateBlock(
                blockToUpdate.copy(
                    isCollapsed = !blockToUpdate.isCollapsed
                ),
                indexToUpdate
            )
        }
    }

    fun recipeFromImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePath = recipeFromImagePath

            if (imagePath != null) {
                isLoading = true

                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                var bitmap = BitmapFactory.decodeFile(imagePath)

                val bitmapSize = bitmap.width * bitmap.height

                if (bitmapSize > imageSizeThreshold) {
                    val scaleFactor = sqrt(imageSizeThreshold.toDouble() / bitmapSize)
                    val scaledWidth = (bitmap.width * scaleFactor).toInt()
                    val scaledHeight = (bitmap.height * scaleFactor).toInt()

                    bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        scaledWidth,
                        scaledHeight,
                        true
                    )
                }

                if (recipeFromImageOrientation != 0) {
                    // Rotate Bitmap to avoid the error from
                    // rotationDegrees parameter in InputImage.fromBitmap:
                    // Even if rotation degrees are passed to the function,
                    // they're not applied

                    val matrix = Matrix().apply {
                        postRotate(recipeFromImageOrientation.toFloat())
                    }

                    bitmap =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                }

                val inputImage = InputImage.fromBitmap(bitmap, 0)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->

                        imageRecipeBlockContainer = ImageRecipeBlockContainer(
                            imageRecipeBlockList = visionText.textBlocks.map { textBlock ->
                                textBlock.lines[0].elements[0].text
                                ImageRecipeBlock(
                                    text = textBlock.text,
                                    elementList = textBlock.lines.flatMap { line ->
                                        line.elements.map { element ->
                                            element.text
                                        }
                                    }
                                )
                            }
                        )
                        isLoading = false
                        isRecipeFromImagePopupOpen = true
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        e.message
                    }
            }
        }
    }

    fun closeRecipeFromImagePopup() {
        isRecipeFromImagePopupOpen = false
    }

    fun loadRecipeFromImageResult(overrideRecipe: Boolean) {
        val nameList: MutableList<String> = mutableListOf()
        var methodList: MutableList<MethodDao> = mutableListOf()
        var ingredientItemList: MutableList<IngredientItemDao> = mutableListOf()

        imageRecipeBlockContainer?.imageRecipeBlockList?.forEach { block ->
            when (block.blockType) {
                ImageRecipeBlockTypes.TITLE -> {
                    nameList.add(block.text)
                }

                ImageRecipeBlockTypes.INGREDIENT_TITlE -> {
                    ingredientItemList.add(
                        IngredientGroupTitleDao(title = block.text)
                    )
                }

                ImageRecipeBlockTypes.INGREDIENT -> {
                    var ingredientName = ""
                    var ingredientQuantity = ""
                    var ingredientValue = ""

                    val quantityIndex = block.quantityElementIndex

                    if (quantityIndex == null) {
                        ingredientName = block.text
                    } else {

                        val listLen = block.elementList.size
                        val valueStartIndex = min(quantityIndex + 1, listLen)


                        ingredientName = block.elementList
                            .subList(0, quantityIndex)
                            .joinToString(separator = " ")
                        ingredientQuantity = block.elementList[quantityIndex]
                        ingredientValue = block.elementList
                            .subList(valueStartIndex, listLen)
                            .joinToString(separator = " ")
                    }


                    ingredientItemList.add(
                        IngredientDao(
                            name = ingredientName,
                            quantity = ingredientQuantity,
                            value = ingredientValue
                        )
                    )
                }

                ImageRecipeBlockTypes.METHOD -> {
                    methodList.add(
                        MethodDao(value = block.text)
                    )
                }

                null -> { /* Do nothing */
                }
            }
        }

        var name = nameList.joinToString(separator = " ")

        if (overrideRecipe == false) {
            name = recipeUiState.recipeDao.name + " " + name
            name = name.trim()

            if (
                recipeUiState.recipeDao.methodList.size > 1 ||
                recipeUiState.recipeDao.methodList[0].value.isNotEmpty()
            ) {
                methodList = (recipeUiState.recipeDao.methodList + methodList).toMutableList()
            }

            if (
                recipeUiState.recipeDao.ingredientItemList.size > 1 ||
                recipeUiState.recipeDao.ingredientItemList[0].isEmpty() == false
            ) {
                ingredientItemList =
                    (recipeUiState.recipeDao.ingredientItemList + ingredientItemList).toMutableList()
            }
        }

        updateUiState(
            recipeUiState.recipeDao.copy(
                name = name,
                methodList = methodList,
                ingredientItemList = ingredientItemList
            )
        )
    }

    //endregion

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}