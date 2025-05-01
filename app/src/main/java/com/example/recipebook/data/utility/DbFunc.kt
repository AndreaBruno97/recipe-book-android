package com.example.recipebook.data.utility

import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class DbFunc {
    companion object {
        inline fun <reified T : RealmObject> getAll(realm: Realm): Flow<List<T>> {
            return realm.query<T>().asFlow().map { it.list }
        }

        inline fun <reified T : RealmObject> getById(realm: Realm, _id: ObjectId): Flow<T?> {
            return realm
                .query<T>("_id = $0", _id)
                .asFlow()
                .map { it.list.firstOrNull() }
        }

        suspend inline fun <reified T : RealmObject> create(
            realm: Realm,
            record: T,
            crossinline getId: (T) -> ObjectId
        ): ObjectId {
            return realm.write {
                val createdRecord = copyToRealm(record, UpdatePolicy.ALL)
                return@write getId(createdRecord)
            }
        }

        suspend inline fun <reified T : RealmObject> update(realm: Realm, record: T) {
            realm.write {
                copyToRealm(record, UpdatePolicy.ALL)
            }
        }

        suspend inline fun <reified T : RealmObject> delete(realm: Realm, record: T) {
            realm.write {
                val latestRecord = findLatest(record) ?: return@write
                delete(latestRecord)
            }
        }
    }
}