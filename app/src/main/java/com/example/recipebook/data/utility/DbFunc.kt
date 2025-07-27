package com.example.recipebook.data.utility

import android.content.Context
import android.net.Uri
import com.example.recipebook.constants.FileConstants
import com.example.recipebook.constants.FileFunctions
import com.example.recipebook.data.builder.DbBuilder
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.io.File

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

        fun closeDb(realm: Realm) {
            realm.close()
            Realm.deleteRealm(realm.configuration)
        }

        fun getDbDownloadFile(context: Context): File {
            var tmpDownloadFile = File(context.cacheDir, FileConstants.DOWNLOAD_DB_FILE)

            // Create folder if it doesn't exist
            tmpDownloadFile.createNewFile()

            // Retrieve source files
            val dbFile = File(context.filesDir, FileConstants.DB_FULL_NAME)
            val recipeFileFolder = File(context.filesDir, FileConstants.RECIPES_FOLDER)
            val contentList = listOf(dbFile, recipeFileFolder)

            // Get zipped file
            tmpDownloadFile = FileFunctions.zipFile(contentList, tmpDownloadFile)

            return tmpDownloadFile
        }

        fun loadDbFile(dbFileUri: Uri, realm: Realm, context: Context): Realm? {
            val unzippedFolder = File(context.cacheDir, FileConstants.LOAD_DB_FOLDER)
            unzippedFolder.mkdir()
            FileFunctions.unzipFile(dbFileUri, unzippedFolder.path, context)
            val unzippedFolderContent = unzippedFolder.listFiles()

            val dbFile = File(context.filesDir, FileConstants.DB_FULL_NAME)
            val recipeFileFolder = File(context.filesDir, FileConstants.RECIPES_FOLDER)

            val sourceDbFile = unzippedFolderContent
                ?.firstOrNull { file ->
                    file.isFile && file.name.endsWith(FileConstants.DB_NAME_EXTENSION)
                }

            val sourceRecipeFileFolder = unzippedFolderContent
                ?.firstOrNull { file ->
                    file.isDirectory
                }

            if (sourceDbFile != null && sourceRecipeFileFolder != null) {
                closeDb(realm)

                dbFile.delete()
                sourceDbFile.copyTo(dbFile)
                FileFunctions.deleteDirectory(recipeFileFolder)
                FileFunctions.deepCopyDirectory(sourceRecipeFileFolder, recipeFileFolder)

                return DbBuilder.getDb()
            } else {
                return null
            }
        }
    }
}