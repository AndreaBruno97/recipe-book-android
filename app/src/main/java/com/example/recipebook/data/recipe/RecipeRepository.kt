package com.example.recipebook.data.recipe

import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class RecipeRepository(private val realm: Realm) {

    fun getRecipes(): Flow<List<Recipe>>{
        return realm.query<Recipe>().asFlow().map { it.list }
    }

    fun getRecipeById(id: ObjectId): Flow<Recipe?>{
        return realm
            .query<Recipe>("_id = $0", id)
            .asFlow()
            .map { it.list.firstOrNull() }
    }

    suspend fun addRecipe(recipe: Recipe): ObjectId{
        return realm.write {
            var createdRecipe = copyToRealm(recipe)
            return@write createdRecipe._id
        }
    }

    suspend fun updateRecipe(recipe: Recipe){
        realm.write {
            copyToRealm(recipe, UpdatePolicy.ALL)
        }
    }

    suspend fun removeRecipe(recipe: Recipe){
        realm.write {
            val latestRecipe = findLatest(recipe) ?: return@write
            delete(latestRecipe)
        }
    }

}