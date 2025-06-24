package com.example.recipebook.network.utility

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.net.URL

class Util private constructor() {
    companion object {
        //region Document

        fun elementGetText(element: Element, cssQuery: String): String? {
            val text = element.select(cssQuery).firstOrNull()?.text()

            return text
        }

        fun elementGetTextWithoutChildren(element: Element): String {
            val elementString = element
                .childNodes()
                .filterIsInstance<TextNode>() // Removes "span" numbers
                .joinToString(
                    separator = ""
                )

            return elementString
        }

        fun elementGetInt(
            element: Element,
            cssQuery: String,
            pattern: String,
            groupIndex: Int
        ): Int? {
            val stringValue = elementGetText(element, cssQuery) ?: return null

            val intValue = regexGetInt(pattern, stringValue, groupIndex)

            return intValue
        }

        fun elementGetTimeMinutes(
            element: Element,
            cssQuery: String,
            pattern: String,
            hourIndex: Int,
            minuteIndex: Int
        ): Int? {
            val stringValue = elementGetText(element, cssQuery) ?: return null

            val hourValue = regexGetInt(pattern, stringValue, hourIndex)
            val minuteValue = regexGetInt(pattern, stringValue, minuteIndex)

            if (hourValue == null && minuteValue == null) {
                return null
            }

            val hour = hourValue ?: 0
            val minute = minuteValue ?: 0

            return (hour * 60) + minute
        }

        //endregion

        //region Regex

        private fun regexGetInt(pattern: String, input: String, groupIndex: Int): Int? {
            val regex = Regex(pattern)
            val intValue = regex
                .find(input)
                ?.groups?.get(groupIndex)
                ?.value
                ?.toIntOrNull()

            return intValue
        }

        //endregion

        //region Image Retrieval

        fun getImageByteFromUrl(url: String): ByteArray? {
            val imageUrl = URL(url)
            val imageBytes = imageUrl.readBytes()

            if (imageBytes.isEmpty()) {
                return null
            }

            return imageBytes
        }

        //endregion
    }
}