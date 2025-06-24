package com.example.recipebook.network.parsers

import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.network.utility.Util
import io.realm.kotlin.ext.toRealmList
import org.jsoup.nodes.Element

object GialloZafferanoParser : BaseWebsiteParser {
    override fun getTitle(element: Element): String? {
        val pageTitle = Util.elementGetText(
            element,
            ".gz-title-recipe, h1"
        )

        return pageTitle
    }

    override fun getServingsNum(element: Element): Int? {
        val featuredDataList = element
            .select(".gz-list-featured-data ul li")

        val servingsNumElement = featuredDataList[3]

        val servingsNum = Util.elementGetInt(
            servingsNumElement,
            ".gz-name-featured-data strong",
            """^(\d+).*$""",
            1
        )

        return servingsNum
    }

    override fun getPrepTimeMinutes(element: Element): Int? {
        val featuredDataList = element
            .select(".gz-list-featured-data ul li")

        val prepTimeMinutesElement = featuredDataList[1]

        val prepTimeMinutes = Util.elementGetTimeMinutes(
            prepTimeMinutesElement,
            ".gz-name-featured-data strong",
            """^((\d+) h)?( )?((\d+) min)?$""",
            2,
            5
        )

        return prepTimeMinutes
    }

    override fun getCookTimeMinutes(element: Element): Int? {
        val featuredDataList = element
            .select(".gz-list-featured-data ul li")

        val cookTimeMinutesElement = featuredDataList[2]

        val cookTimeMinutes = Util.elementGetTimeMinutes(
            cookTimeMinutesElement,
            ".gz-name-featured-data strong",
            """^((\d+) h)?( )?((\d+) min)?$""",
            2,
            5
        )

        return cookTimeMinutes
    }

    override fun getMethodList(element: Element): List<String> {
        val methodList = mutableListOf<String>()

        val contentRecipeElements = element.select(".gz-content-recipe")
        val storageTitleElementList = element.select(".gz-inner .gz-title-section")

        // Methods
        element.select(".gz-content-recipe-step p")
            .forEach {
                val methodString = Util.elementGetTextWithoutChildren(it)

                methodList.add(methodString)
            }

        // Storage
        if (contentRecipeElements.size >= 3) {
            var contentString = ""
            if (storageTitleElementList.size >= 4) {
                contentString += storageTitleElementList[3].text() + "\n\n"
            }
            val storageElement = contentRecipeElements[2]

            contentString += storageElement
                .select("div p")
                .joinToString(separator = "\n\n") {
                    it.text()
                }

            methodList.add(contentString)
        }

        // Suggestions
        if (contentRecipeElements.size >= 4) {
            var suggestionString = ""
            if (storageTitleElementList.size >= 5) {
                suggestionString += storageTitleElementList[4].text() + "\n\n"
            }
            val suggestionElement = contentRecipeElements[3]

            suggestionString += suggestionElement
                .select("div p")
                .joinToString(separator = "\n\n") {
                    it.text()
                }

            methodList.add(suggestionString)
        }

        return methodList
    }

    override fun getIngredientGroupList(element: Element): List<IngredientGroup> {
        val ingredientGroupList = mutableListOf<IngredientGroup>()

        element.select(".gz-list-ingredients")
            .forEach { ingredientGroupElement ->
                val curIngredientList: MutableList<Ingredient> = mutableListOf()

                val ingredientGroupTitle = Util.elementGetText(
                    ingredientGroupElement,
                    ".gz-title-ingredients"
                )

                ingredientGroupElement.select(".gz-ingredient")
                    .forEach { ingredientElement ->
                        val ingredientName = Util.elementGetText(ingredientElement, "a")
                        val ingredientDetail = Util.elementGetText(ingredientElement, "span")
                        var ingredientQuantity: Float? = null
                        var ingredientValue: String? = null

                        val regex = Regex("""^ *((\d|\,)*) ?(.*)$""")
                        if (ingredientDetail != null) {
                            val regexGroups = regex.find(ingredientDetail)?.groups

                            if (regexGroups != null) {
                                if (regexGroups.size > 1) {
                                    ingredientQuantity = regexGroups[1]
                                        ?.value
                                        ?.replace(',', '.')
                                        ?.toFloatOrNull()
                                }

                                if (regexGroups.size > 3) {
                                    ingredientValue = regexGroups[3]?.value
                                }
                            }
                        }

                        curIngredientList.add(
                            Ingredient(
                                name = ingredientName ?: "",
                                quantity = ingredientQuantity,
                                value = ingredientValue ?: ""
                            )
                        )
                    }

                ingredientGroupList.add(
                    IngredientGroup(
                        title = ingredientGroupTitle,
                        ingredientList = curIngredientList.toRealmList()
                    )
                )
            }

        return ingredientGroupList
    }

    override fun getImageBytes(element: Element): ByteArray? {
        /*
        val recipeElementList = element
            .select(".gz-content-recipe")
            .firstOrNull() ?: return null

        val recipeImageElement = recipeElementList
            .select("picture img")
            .firstOrNull() ?: return null

        val imageUrlString = recipeImageElement
            .attr("src") ?: return null

         */
        var recipeImageElement = element
            .select(".gz-content picture img")
            .firstOrNull()
        var imageUrlString: String? = null

        if (recipeImageElement != null) {
            imageUrlString = recipeImageElement
                .attr("src")
        } else {
            recipeImageElement = element
                .select(".gz-content-recipe picture img")
                .firstOrNull()

            imageUrlString = recipeImageElement
                ?.attr("src")
        }

        if (imageUrlString == null) {
            return null
        }

        val imageBytes = Util.getImageByteFromUrl(imageUrlString)

        return imageBytes
    }
}