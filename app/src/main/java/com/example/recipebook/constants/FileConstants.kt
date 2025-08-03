package com.example.recipebook.constants

class FileConstants {
    companion object {
        const val DB_NAME = "recipeDb"
        const val DB_NAME_EXTENSION = ".realm"
        const val DB_FULL_NAME = DB_NAME + DB_NAME_EXTENSION
        const val RECIPES_FOLDER = "recipeFiles"
        const val RECIPE_IMAGE_FILE_NAME_PREFIX = "recipeImage"
        const val RECIPE_IMAGE_FILE_NAME_SUFFIX = ".jpg"
        const val TEMP_IMAGE_PREFIX = "temp_image_file_"
        const val TEMP_IMAGE_SUFFIX = ".jpg"
        const val DOWNLOAD_DB_FILE_PREFIX = "recipe_db"
        const val DOWNLOAD_DB_FILE_SUFFIX = ".zip"
        const val LOAD_DB_FOLDER = "tempLoadDbFolder"
        const val BACKUP_IN_APP_LOCAL_STORAGE_FOLDER_NAME = "backup"

        const val BACKUP_INTERVAL_HOURS: Long = 24 * 1 // 1 Day
    }
}