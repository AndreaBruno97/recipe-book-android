package com.example.recipebook.data.builder

import com.example.recipebook.data.objects.tag.Tag
import io.realm.kotlin.InitialDataCallback
import io.realm.kotlin.MutableRealm

class DbInitialDataCallback : InitialDataCallback {
    private val tagList = listOf(
        Tag(name = "Antipasti"),
        Tag(name = "Primi"),
        Tag(name = "Secondi"),
        Tag(name = "Dolci"),
        Tag(name = "Vegan"),
        Tag(name = "Carne"),
        Tag(name = "Pesce")
    )

    override fun MutableRealm.write() {
        for (tag in tagList) {
            copyToRealm(tag)
        }
    }
}