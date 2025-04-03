package com.example.recipebook

import android.app.Application
import com.example.recipebook.data.builder.DbBuilder
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.TagRepository

class RecipeBookApplication : Application() {

    companion object {
        lateinit var recipeRepository: RecipeRepository
        lateinit var tagRepository: TagRepository
    }

    override fun onCreate() {
        super.onCreate()
        val realm = DbBuilder.getDb()

        recipeRepository = RecipeRepository(realm)
        tagRepository = TagRepository(realm)
    }
}