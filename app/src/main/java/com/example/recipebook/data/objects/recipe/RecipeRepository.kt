package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.utility.DbFunc
import io.realm.kotlin.Realm
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

class RecipeRepository(private val realm: Realm) {

    fun getRecipes(): Flow<List<Recipe>> {
        return DbFunc.getAll(realm)
    }

    fun getRecipeById(_id: ObjectId): Flow<Recipe?> {
        return DbFunc.getById(realm, _id)
    }

    suspend fun addRecipe(recipe: Recipe): ObjectId {
        return DbFunc.create(realm, recipe) { it._id }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        DbFunc.update(realm, recipe)
    }

    suspend fun removeRecipe(recipe: Recipe) {
        DbFunc.delete(realm, recipe)
    }

}