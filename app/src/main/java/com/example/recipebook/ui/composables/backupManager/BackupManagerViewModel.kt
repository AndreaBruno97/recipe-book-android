package com.example.recipebook.ui.composables.backupManager

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    val reloadDbFile: (Uri, Context) -> Unit,
    val reloadDbFileFromLocalBackup: (Context) -> Unit
) : ViewModel() {

    var backupManagerUiState by mutableStateOf(BackupManagerUiState())
        private set

    fun getLastLocalBackupDate(context: Context) {
        val backupFile = DbFunc.getLastBackupFromAppLocalStorage(context)

        backupManagerUiState = backupManagerUiState.copy(
            lastBackupDate = backupFile?.lastModified(),
            loadLastBackupDate = false
        )
    }

    fun getDbDownloadFile(context: Context): File {
        return DbFunc.getDbDownloadFile(context)
    }

    fun loadDbFile(selectedFileUri: Uri, context: Context) {
        reloadDbFile(selectedFileUri, context)
    }

    fun updateLocalBackup(successMessage: String, context: Context) {
        DbFunc.updateBackupInAppLocalStorage(context)
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
        getLastLocalBackupDate(context)
    }

    fun loadLocalBackup(successMessage: String, context: Context) {
        reloadDbFileFromLocalBackup(context)
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
    }
}

data class BackupManagerUiState(
    val lastBackupDate: Long? = null,
    val loadLastBackupDate: Boolean = true
)