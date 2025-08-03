package com.example.recipebook.constants

import androidx.compose.ui.text.intl.Locale
import java.text.SimpleDateFormat
import java.util.Date

class DateFunctions {
    companion object {
        fun getLocaleDateString(date: Date, pattern: String): String {
            val currentLocale = java.util.Locale(Locale.current.language)
            val dateFormatter = SimpleDateFormat(pattern, currentLocale)
            val formattedDate = dateFormatter.format(date)

            return formattedDate
        }

        fun getCurrentLocaleDateString(pattern: String): String {
            val currentDate = Date()
            val formattedDate = getLocaleDateString(currentDate, pattern)

            return formattedDate
        }

        fun getLocaleDateStringFromLong(dateLong: Long, pattern: String): String {
            val date = Date(dateLong)
            val formattedDate = getLocaleDateString(date, pattern)

            return formattedDate
        }
    }
}