package com.example.recipebook.data.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.recipebook.data.objects.tag.Tag
import io.realm.kotlin.InitialDataCallback
import io.realm.kotlin.MutableRealm

class DbInitialDataCallback : InitialDataCallback {
    private val tagList = listOf(
        Tag(
            name = "Antipasti",
            color = Color.Blue.toArgb(),
            icon = "\uD83C\uDF79"
        ),
        Tag(
            name = "Primi",
            color = Color.Red.toArgb(),
            icon = "1\uFE0F"
        ),
        Tag(
            name = "Secondi",
            color = Color.Black.toArgb(),
            icon = "2\uFE0F⃣"
        ),
        Tag(
            name = "Dolci",
            color = Color.Blue.toArgb(),
            icon = "\uD83C\uDF70"
        ),
        Tag(
            name = "Vegan",
            color = Color.Green.toArgb(),
            icon = "\uD83C\uDF3F"
        ),
        Tag(
            name = "Carne",
            color = Color.Red.toArgb(),
            icon = "\uD83C\uDF56"
        ),
        Tag(
            name = "Pesce",
            color = Color.Blue.toArgb(),
            icon = "\uD83D\uDC1F"
        )
    )

    override fun MutableRealm.write() {
        for (tag in tagList) {
            copyToRealm(tag)
        }
    }
}