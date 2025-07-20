package com.example.recipebook.network.parsers

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.recipebook.constants.FileConstants
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.network.NetworkResponse
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import org.jsoup.nodes.Element
import java.io.File

interface BaseWebsiteParser {
    fun getRecipe(element: Element, cacheDirectory: File): NetworkResponse {

        try {
            val title = try {
                getTitle(element)
            } catch (_: Exception) {
                null
            }
            val servingsNum = try {
                getServingsNum(element)
            } catch (e: Exception) {
                null
            }
            val prepTimeMinutes = try {
                getPrepTimeMinutes(element)
            } catch (_: Exception) {
                null
            }
            val cookTimeMinutes = try {
                getCookTimeMinutes(element)
            } catch (_: Exception) {
                null
            }
            val methodList = try {
                getMethodList(element)
            } catch (_: Exception) {
                null
            }
            val ingredientGroupList = try {
                getIngredientGroupList(element)
            } catch (_: Exception) {
                null
            }

            val imageBytes = try {
                getImageBytes(element)
            } catch (_: Exception) {
                null
            }
            var imageBitmap: ImageBitmap? = null
            var imagePath: String? = null

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val tempFile = File.createTempFile(
                    FileConstants.TEMP_IMAGE_PREFIX, /* prefix */
                    FileConstants.TEMP_IMAGE_SUFFIX, /* suffix */
                    cacheDirectory  /* cache directory */
                )
                tempFile.writeBytes(imageBytes)

                imageBitmap = BitmapFactory
                    .decodeByteArray(imageBytes, 0, imageBytes.size)
                    .asImageBitmap()
                imagePath = tempFile.path
            }

            val recipe = Recipe(
                name = title ?: "",

                methodList = methodList?.toRealmList() ?: realmListOf(),
                ingredientGroupList = ingredientGroupList?.toRealmList() ?: realmListOf(),

                servingsNum = servingsNum,
                prepTimeMinutes = prepTimeMinutes,
                cookTimeMinutes = cookTimeMinutes,
            )

            return NetworkResponse(
                success = true,
                recipe = recipe,
                imageBitmap = imageBitmap,
                imagePath = imagePath
            )
        } catch (e: Exception) {
            return NetworkResponse(
                success = false
            )
        }
    }

    fun getTitle(element: Element): String?
    fun getServingsNum(element: Element): Int?
    fun getPrepTimeMinutes(element: Element): Int?
    fun getCookTimeMinutes(element: Element): Int?
    fun getMethodList(element: Element): List<String>
    fun getIngredientGroupList(element: Element): List<IngredientGroup>
    fun getImageBytes(element: Element): ByteArray?
}
