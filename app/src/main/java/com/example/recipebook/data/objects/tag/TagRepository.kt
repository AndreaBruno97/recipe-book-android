package com.example.recipebook.data.objects.tag

import com.example.recipebook.data.utility.DbFunc
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class TagRepository(private val realm: Realm) {

    fun getTag(): Flow<List<Tag>> {
        return DbFunc.getAll(realm)
    }

    fun getTagById(_id: ObjectId): Flow<Tag?> {
        return DbFunc.getById(realm, _id)
    }

    fun getTagFiltered(name: String?): Flow<List<Tag>> {
        var realmQuery = realm.query<Tag>()

        if (name != null) {
            realmQuery = realmQuery
                .query(
                    "name CONTAINS[c] $0", name
                )
        }

        // Sort by name (case insensitive)
        realmQuery = realmQuery.sort("name")

        return realmQuery
            .asFlow()
            .map { it.list }
    }

    suspend fun addTag(tag: Tag): ObjectId {
        return DbFunc.create(realm, tag) { it._id }
    }

    suspend fun updateTag(tag: Tag) {
        DbFunc.update(realm, tag)
    }

    suspend fun removeTag(tag: Tag) {
        DbFunc.delete(realm, tag)
    }

    suspend fun removeTagById(_id: ObjectId) {
        DbFunc.deleteById<Tag>(realm, _id)
    }


    fun isNamePresent(tag: Tag): Boolean {
        val tagWithName = realm
            // [c] means the match is case insensitive
            .query<Tag>("name =[c] $0 && _id != $1", tag.name, tag._id)
            .first().find()

        return tagWithName != null
    }
}