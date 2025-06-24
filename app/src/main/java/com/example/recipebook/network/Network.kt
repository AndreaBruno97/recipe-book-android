package com.example.recipebook.network

import androidx.compose.ui.graphics.ImageBitmap
import com.example.recipebook.data.objects.recipe.Recipe
import com.example.recipebook.network.parsers.BenedettaParser
import com.example.recipebook.network.parsers.GialloZafferanoParser
import org.jsoup.Jsoup
import java.io.File
import java.net.MalformedURLException
import java.net.URL

enum class SupportedWebsites(val host: String) {
    GIALLO_ZAFFERANO("ricette.giallozafferano.it"),
    BENEDETTA("www.fattoincasadabenedetta.it"),
}

data class NetworkResponse(
    val success: Boolean,
    val recipe: Recipe? = null,
    val imageBitmap: ImageBitmap? = null,
    val imagePath: String? = null
)

class Network private constructor() {
    companion object {
        fun getPage(urlString: String, cacheDirectory: File): NetworkResponse {

            try {
                //region URL pre-processing

                val cleanUrlString = if (urlString.last() == '/') {
                    urlString.dropLast(1)
                } else {
                    urlString
                }


                val url = URL(cleanUrlString)

                val website = SupportedWebsites.entries.firstOrNull { it.host == url.host }
                if (website == null) {
                    return NetworkResponse(
                        success = false
                    )
                }

                //endregion

                //region Page Retrieval

                var pageString = ""
                var retryNum = MAX_TRY_NUM

                while (
                    retryNum > 0 &&
                    pageString.isBlank()
                ) {
                    pageString = url.readText()

                    retryNum--
                }

                if (pageString.isBlank()) {
                    return NetworkResponse(
                        success = false
                    )
                }

                val htmlPage = Jsoup.parse(pageString)

                //endregion

                //region Recipe Extraction

                val result = when (website) {
                    SupportedWebsites.GIALLO_ZAFFERANO ->
                        GialloZafferanoParser.getRecipe(htmlPage, cacheDirectory)

                    SupportedWebsites.BENEDETTA ->
                        BenedettaParser.getRecipe(htmlPage, cacheDirectory)
                }

                //endregion

                return result
            } catch (e: MalformedURLException) {
                return NetworkResponse(
                    success = false
                )
            } catch (e: Exception) {
                return NetworkResponse(
                    success = false
                )
            }
        }

        private const val MAX_TRY_NUM = 3
    }
}
