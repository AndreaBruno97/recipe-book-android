package com.example.recipebook.network.parsers

import com.example.recipebook.data.objects.ingredient.Ingredient
import com.example.recipebook.data.objects.ingredientGroup.IngredientGroup
import com.example.recipebook.network.utility.Util
import io.realm.kotlin.ext.toRealmList
import org.jsoup.nodes.Element

object BenedettaParser : BaseWebsiteParser {
    override fun getTitle(element: Element): String? {
        val pageTitle = Util.elementGetText(
            element,
            "h1, .title h1"
        )

        return pageTitle
    }

    override fun getServingsNum(element: Element): Int? {
        val servingsNumString = Util.elementGetText(
            element,
            ".recipe-ingredients .chooser .num"
        )

        val servingsNum = servingsNumString?.toIntOrNull()

        return servingsNum
    }

    override fun getPrepTimeMinutes(element: Element): Int? {
        val recipeTimeList = element
            .select(".recipe-time .time")

        val prepTimeMinutesElement = recipeTimeList[0]

        val prepTimeMinutes = Util.elementGetTimeMinutes(
            prepTimeMinutesElement,
            "dd",
            """^((\d+) or.)?( e )?((\d+) minut.)?$""",
            2,
            5
        )

        return prepTimeMinutes
    }

    override fun getCookTimeMinutes(element: Element): Int? {
        val recipeTimeList = element
            .select(".recipe-time .time")

        val prepTimeMinutesElement = recipeTimeList[1]

        val prepTimeMinutes = Util.elementGetTimeMinutes(
            prepTimeMinutesElement,
            "dd",
            """^((\d+) or.)?( e )?((\d+) minut.)?$""",
            2,
            5
        )

        return prepTimeMinutes
    }

    override fun getMethodList(element: Element): List<String> {
        val methodList = mutableListOf<String>()

        element.select(".recipe-preparation .steps .step .content p")
            .forEach {
                methodList.add(it.text())
            }

        return methodList
    }

    override fun getIngredientGroupList(element: Element): List<IngredientGroup> {
        val ingredientGroupList = mutableListOf<IngredientGroup>()


        element.select(".recipe-ingredients .group")
            .forEach { ingredientGroupElement ->
                val curIngredientList: MutableList<Ingredient> = mutableListOf()

                val ingredientGroupTitle = Util.elementGetText(
                    ingredientGroupElement,
                    "h3, .caption"
                )

                ingredientGroupElement.select("li")
                    .forEach { ingredientElement ->
                        val ingredientNameElement =
                            ingredientElement.select(".ingredient").firstOrNull()
                        val ingredientName = if (ingredientNameElement != null) {
                            Util
                                .elementGetTextWithoutChildren(ingredientNameElement)
                                .trim()
                        } else {
                            null
                        }
                        val ingredientNameDescription =
                            Util.elementGetText(ingredientElement, ".ingredient em")

                        val ingredientQuantityElement = ingredientElement
                            .select(".quantity .quantity-num")
                            .firstOrNull()

                        val ingredientQuantity = ingredientQuantityElement
                            ?.attr("data-original")
                            ?.toFloatOrNull()


                        val ingredientQuantityMeasureUnit = ingredientQuantityElement
                            ?.attr("data-um")

                        val ingredientValueStringList: List<String?> =
                            listOf(ingredientQuantityMeasureUnit, ingredientNameDescription)
                        val ingredientValue = ingredientValueStringList
                            .joinToString(separator = " ") { it ?: "" }
                            .trim()

                        curIngredientList.add(
                            Ingredient(
                                name = ingredientName ?: "",
                                quantity = ingredientQuantity,
                                value = ingredientValue
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
        var recipeImageElement = element
            .select(".recipe-intro .presentation .image embed")
            .firstOrNull()
        var imageUrlString: String? = null

        if (recipeImageElement != null) {
            imageUrlString = recipeImageElement
                .attr("src")
        } else {
            recipeImageElement = element
                .select(".recipe-intro .presentation .image img")
                .firstOrNull()

            imageUrlString = recipeImageElement
                ?.attr("data-lazy-src")
        }

        if (imageUrlString == null) {
            return null
        }

        val imageBytes = Util.getImageByteFromUrl(imageUrlString)

        return imageBytes
    }
}