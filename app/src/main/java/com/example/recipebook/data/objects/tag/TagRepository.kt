package com.example.recipebook.data.objects.tag

import com.example.recipebook.data.utility.DbFunc
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

class TagRepository(private val realm: Realm) {

    fun getTag(): Flow<List<Tag>> {
        return DbFunc.getAll(realm)
    }

    fun getTagById(_id: ObjectId): Flow<Tag?> {
        return DbFunc.getById(realm, _id)
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

    fun isNamePresent(tag: Tag): Boolean {
        val tagWithName = realm
            // [c] means the match is case insensitive
            .query<Tag>("name =[c] $0 && _id != $1", tag.name, tag._id)
            .first().find()

        return tagWithName != null
    }
}