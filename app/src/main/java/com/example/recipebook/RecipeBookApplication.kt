package com.example.recipebook

import android.app.Application
import com.example.recipebook.data.DbBuilder
import com.example.recipebook.data.recipe.Recipe
import com.example.recipebook.data.recipe.RecipeRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class RecipeBookApplication: Application() {

    companion object{
        lateinit var recipeRepository: RecipeRepository
    }

    override fun onCreate() {
        super.onCreate()
        val realm = DbBuilder.getDb()

        recipeRepository = RecipeRepository(realm)
    }
}