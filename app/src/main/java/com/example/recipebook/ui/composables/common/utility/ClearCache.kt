package com.example.recipebook.ui.composables.common.utility

import android.content.Context
import java.io.File

fun clearCache(context: Context) {
    val cacheDirectory = context.cacheDir

    deleteDirectory(cacheDirectory)
}

private fun deleteDirectory(directory: File) {
    if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        directory.delete()
    }
}