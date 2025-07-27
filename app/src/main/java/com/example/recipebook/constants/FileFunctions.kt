package com.example.recipebook.constants

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileFunctions {
    companion object {
        fun clearCache(context: Context) {
            val cacheDirectory = context.cacheDir

            deleteDirectory(cacheDirectory)
        }

        fun deleteDirectory(directory: File) {
            if (directory.exists() && directory.isDirectory) {
                directory.walkBottomUp().forEach { file ->
                    // walkBottomUp visits folders after the files they contain
                    // so the files can be deleted before their folder
                    file.delete()
                }
                directory.delete()
            }
        }

        fun deepCopyDirectory(sourceDirectory: File, destinationDirectory: File) {
            if (sourceDirectory.exists() && sourceDirectory.isDirectory) {
                if (destinationDirectory.exists() == false) {
                    destinationDirectory.mkdir()
                }

                // walkTopDown visits folders before the files they contain
                // so the folders can be copied before their content
                sourceDirectory.walkTopDown().forEach { sourceFile ->
                    val relativeFile = sourceFile.relativeTo(sourceDirectory)
                    val destinationFile = File(destinationDirectory, relativeFile.path)
                    if (sourceFile.isDirectory) {
                        destinationFile.mkdir()
                    } else {
                        sourceFile.copyTo(destinationFile)
                    }
                }
            }
        }

        fun zipFile(files: List<File>, outputZipFile: File): File {
            /*
             Transform the list of files and folders
             into a list of only files:
             folders are recursively unpacked
             Files are paired to their relative path
             based on the starting folder
             in order to maintain the subfolder structure
             even in the zipped main folder
             */

            val filesFromFolder: List<Pair<File, String>> = files.flatMap { file ->
                if (!file.exists()) {
                    listOf(null)
                } else if (file.isFile) {
                    listOf(Pair(file, file.name))
                } else {
                    file.walkTopDown().map { innerFile ->
                        if (innerFile.exists() && innerFile.isFile) {
                            val relativeFile = innerFile.relativeTo(file)
                            val relativeFilePath = file.name + "/" + relativeFile.path
                            Pair(innerFile, relativeFilePath)
                        } else {
                            null
                        }
                    }.toList()
                }
            }.filterNotNull()

            ZipOutputStream(FileOutputStream(outputZipFile)).use { zipOut ->
                filesFromFolder.forEach { (file, filePath) ->
                    FileInputStream(file).use { fileIn ->
                        val zipEntry = ZipEntry(filePath)
                        zipOut.putNextEntry(zipEntry)
                        fileIn.copyTo(zipOut)
                        zipOut.closeEntry()
                    }
                }
            }

            return outputZipFile
        }

        fun unzipFile(zipFileUri: Uri, destinationDir: String, context: Context) {
            val destinationDirFile = File(destinationDir).canonicalFile
            val inputStream = context.contentResolver.openInputStream(zipFileUri)

            ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val newFile = File(destinationDirFile, entry.name).canonicalFile

                    if (entry.isDirectory) {
                        newFile.mkdirs()
                    } else if (newFile.parentFile != null) {
                        newFile.parentFile?.mkdirs()
                        FileOutputStream(newFile).use { fos -> zipIn.copyTo(fos) }
                    }

                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
        }

        fun saveToDownloadFolder(
            file: File,
            successMessage: String,
            context: Context
        ) {
            val downloadFolder: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Storing the data in file with name
            // avoiding duplicates (adding number at the end of the file name)
            var currentIncrement = 0
            var destinationFile: File

            do {
                val currentFileName = if (currentIncrement == 0) {
                    file.name
                } else {
                    "${file.nameWithoutExtension} (${currentIncrement}).${file.extension}"
                }

                destinationFile = File(downloadFolder, currentFileName)
                currentIncrement++

            } while (destinationFile.exists())

            file.copyTo(destinationFile)

            // displaying a toast message
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun saveToDownloadFolderComposable(
    context: Context,
    successMessage: String,
    getFileToSave: () -> File
): () -> Unit {
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                // Save file
                val fileToSave = getFileToSave()
                FileFunctions.saveToDownloadFolder(fileToSave, successMessage, context)
            } else {
                // Can't save file
            }
        }

    return {
        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}


@Composable
fun selectFileComposable(
    context: Context,
    getSelectedFile: (Uri, Context) -> Unit
): () -> Unit {
    val getContentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { fileUri ->
            if (fileUri != null) {
                // Copy file in cache
                getSelectedFile(fileUri, context)
            } else {
                // No file selected
            }
        }

    return {
        getContentLauncher.launch("*/*")
    }
}