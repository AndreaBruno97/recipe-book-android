package com.example.recipebook.data.objects.recipe

import com.example.recipebook.data.utility.DbFunc
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class RecipeRepository(private val realm: Realm) {

    fun getRecipes(): Flow<List<Recipe>> {
        return DbFunc.getAll(realm)
    }

    fun getRecipeById(_id: ObjectId): Flow<Recipe?> {
        return DbFunc.getById(realm, _id)
    }

    fun getRecipesFiltered(name: String?, isFavorite: Boolean): Flow<List<Recipe>> {
        var realmQuery = realm.query<Recipe>()

        if (name != null) {
            realmQuery = realmQuery.query(
                "name CONTAINS[c] $0", name
            )
        }

        if (isFavorite) {
            realmQuery = realmQuery.query(
                "isFavorite == true"
            )
        }

        return realmQuery
            .asFlow()
            .map { it.list }
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

    suspend fun setIsFavorite(_id: ObjectId, isFavorite: Boolean) {
        val recipeToUpdate = realm
            .query<Recipe>("_id = $0", _id)
            .find()
            .first()

        realm.write {
            findLatest(recipeToUpdate)?.isFavorite = isFavorite
        }
    }

}