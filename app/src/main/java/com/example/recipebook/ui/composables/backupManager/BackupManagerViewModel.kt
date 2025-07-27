package com.example.recipebook.ui.composables.backupManager

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.recipebook.data.utility.DbFunc
import java.io.File

/*
    NEVER pass DB repositories
    The DB restore functionality requires the DB
    to be closed and re-opened, thus making the old
    repository object invalid.
    Calling the DB from those objects makes the app crash.
 */

class BackupManagerViewModel(
    val reloadDbFile: (Uri, Context) -> Unit
) : ViewModel() {

    fun getDbDownloadFile(context: Context): File {
        return DbFunc.getDbDownloadFile(context)
    }

    fun loadDbFile(selectedFileUri: Uri, context: Context) {
        reloadDbFile(selectedFileUri, context)
    }
}