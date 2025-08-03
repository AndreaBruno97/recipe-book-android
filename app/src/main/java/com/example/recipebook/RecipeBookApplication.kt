package com.example.recipebook

import android.app.Application
import android.content.Context
import android.net.Uri
import com.example.recipebook.data.builder.DbBuilder
import com.example.recipebook.data.objects.recipe.RecipeRepository
import com.example.recipebook.data.objects.tag.TagRepository
import com.example.recipebook.data.utility.DbFunc
import com.example.recipebook.workers.PeriodicBackupWorkerManager
import io.realm.kotlin.Realm

class RecipeBookApplication : Application() {

    companion object {
        private lateinit var realm: Realm
        lateinit var recipeRepository: RecipeRepository
        lateinit var tagRepository: TagRepository
        lateinit var periodicBackupWorkerManager: PeriodicBackupWorkerManager

        fun reloadDb(dbFileUri: Uri, context: Context) {
            val realmInstance = DbFunc.loadDbFile(dbFileUri, realm, context)
            if (realmInstance != null) {
                loadDb(realmInstance)
            }
        }

        private fun loadDb(realmInstance: Realm) {
            realm = realmInstance
            recipeRepository = RecipeRepository(realm)
            tagRepository = TagRepository(realm)
        }

        fun reloadDbFileFromLocalBackup(context: Context) {
            val backupFile = DbFunc.getLastBackupFromAppLocalStorage(context)

            if (backupFile != null && backupFile.isFile) {
                val backupFileUri = Uri.fromFile(backupFile)

                reloadDb(backupFileUri, context)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val realmInstance = DbBuilder.getDb()
        loadDb(realmInstance)

        periodicBackupWorkerManager = PeriodicBackupWorkerManager(this)
        periodicBackupWorkerManager.startPeriodicBackup()
    }
}