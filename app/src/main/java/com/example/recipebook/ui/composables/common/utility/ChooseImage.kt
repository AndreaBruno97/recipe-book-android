package com.example.recipebook.ui.composables.common.utility

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.io.File
import java.io.FileOutputStream

sealed class Intent {
    data class OnPermissionGrantedWith(val compositionContext: Context) : Intent()
    data object OnPermissionDenied : Intent()
    data class OnImageSavedWith(val compositionContext: Context) : Intent()
    data object OnImageSavingCanceled : Intent()
    data class OnFinishPickingImagesWith(
        val compositionContext: Context,
        val imageUrls: List<Uri>
    ) : Intent()
}

fun getRecipeFolderPath(recipeId: ObjectId): String {
    return "recipeFiles/${recipeId.toHexString()}"
}

fun getRecipeImagePath(recipeId: ObjectId): String {
    val recipeFolderPath = getRecipeFolderPath(recipeId)
    val recipeFileName = "recipeImage.jpg"
    return "${recipeFolderPath}/${recipeFileName}"
}

fun loadRecipeImage(recipeId: ObjectId, context: Context): ImageBitmap? {
    var recipeImage: ImageBitmap? = null
    val recipeImagePath = getRecipeImagePath(recipeId)
    val recipeImageFile = File(context.filesDir, recipeImagePath)

    if (recipeImageFile.isFile) {
        val bitmapOptions = BitmapFactory.Options()
        val recipeBytes = recipeImageFile.readBytes()
        val bitmap: Bitmap =
            BitmapFactory.decodeByteArray(recipeBytes, 0, recipeBytes.size, bitmapOptions)
        recipeImage = bitmap.asImageBitmap()
    }

    return recipeImage
}

open class ImageManagerViewModel : ViewModel() {
    var tempFileUrl: Uri? by mutableStateOf(null)
    var tempFileUrlDirectString: String? by mutableStateOf(null)
    var tempImage: ImageBitmap? by mutableStateOf(null)
    var isFileChanged: Boolean by mutableStateOf(false)

    fun clearImage() {
        if (tempImage != null) {
            isFileChanged = true
        }

        tempFileUrl = null
        tempFileUrlDirectString = null
        tempImage = null
    }

    suspend fun saveImage(folderPath: String, filePath: String, context: Context) {
        val sourceFilePath = tempFileUrlDirectString
        val folderFile = File(context.filesDir, folderPath)

        // Create directory if it doesn't exist
        folderFile.mkdirs()

        if (isFileChanged) {
            val imageToStore = tempImage
            val imageFile = File(context.filesDir, filePath)

            // Clear directory in case previous file exists
            folderFile.listFiles()?.forEach { it.delete() }

            // Create new file
            if (sourceFilePath != null && imageToStore != null) {
                val fileBytes = File(sourceFilePath).readBytes()
                withContext(Dispatchers.IO) {
                    FileOutputStream(imageFile).use {
                        it.write(fileBytes)
                    }
                }
            }
        }
    }

    fun onReceive(intent: Intent) = runBlocking {
        viewModelScope.launch(coroutineContext) {
            when (intent) {
                is Intent.OnPermissionGrantedWith -> {
                    // Create an empty image file in the app's cache directory
                    val tempFile = File.createTempFile(
                        "temp_image_file_", /* prefix */
                        ".jpg", /* suffix */
                        intent.compositionContext.cacheDir  /* cache directory */
                    )

                    // Create sandboxed url for this temp file - needed for the camera API
                    val uri = FileProvider.getUriForFile(
                        intent.compositionContext,
                        "${BuildConfig.APPLICATION_ID}.provider", /* needs to match the provider information in the manifest */
                        tempFile
                    )
                    tempFileUrl = uri
                    tempFileUrlDirectString = tempFile.path
                }

                is Intent.OnPermissionDenied -> {
                    // maybe log the permission denial event
                    println("User did not grant permission to use the camera")
                }

                is Intent.OnFinishPickingImagesWith -> {
                    if (intent.imageUrls.isNotEmpty()) {
                        // Handle picked image
                        val imageUrl = intent.imageUrls.first()

                        val inputStream =
                            intent.compositionContext.contentResolver.openInputStream(imageUrl)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            val bitmapOptions = BitmapFactory.Options()
                            bitmapOptions.inMutable = true
                            val bitmap: Bitmap =
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bitmapOptions)
                            tempImage = bitmap.asImageBitmap()

                            val tempFilePicked = File.createTempFile(
                                "temp_image_file_", /* prefix */
                                ".jpg", /* suffix */
                                intent.compositionContext.cacheDir  /* cache directory */
                            )
                            tempFilePicked.writeBytes(bytes)
                            tempFileUrlDirectString = tempFilePicked.path
                            isFileChanged = true
                        } else {
                            // error reading the bytes from the image url
                            println("The image that was picked could not be read from the device at this url: $imageUrl")
                        }

                        tempFileUrl = null
                    } else {
                        // user did not pick anything
                    }
                }

                is Intent.OnImageSavedWith -> {
                    val tempImageUrl = tempFileUrl
                    if (tempImageUrl != null) {
                        tempImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder
                                .createSource(
                                    intent.compositionContext.contentResolver,
                                    tempImageUrl
                                )

                            ImageDecoder.decodeBitmap(source).asImageBitmap()
                        } else {
                            BitmapFactory
                                .decodeFile(tempFileUrlDirectString)
                                .asImageBitmap()
                        }
                        isFileChanged = true
                    }

                    tempFileUrl = null
                }

                is Intent.OnImageSavingCanceled -> {
                    tempFileUrl = null
                }
            }
        }
    }
}

class CameraLauncherStateCollector(
    val pickImageFromAlbumLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    val cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    fun takeImage() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun pickImage() {
        // Image picker does not require special permissions and can be activated right away
        val mediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        pickImageFromAlbumLauncher.launch(mediaRequest)
    }
}

@Composable
fun createCameraLauncherState(
    currentContext: Context,
    viewModel: ImageManagerViewModel
): CameraLauncherStateCollector {
    // launches photo picker
    val pickImageFromAlbumLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { urls ->
            viewModel.onReceive(Intent.OnFinishPickingImagesWith(currentContext, urls))
        }

    // launches camera
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
            if (isImageSaved) {
                viewModel.onReceive(Intent.OnImageSavedWith(currentContext))
            } else {
                // handle image saving error or cancellation
                viewModel.onReceive(Intent.OnImageSavingCanceled)
            }
        }

    // launches camera permissions
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                viewModel.onReceive(Intent.OnPermissionGrantedWith(currentContext))
            } else {
                // handle permission denied such as:
                viewModel.onReceive(Intent.OnPermissionDenied)
            }
        }

    // this ensures that the camera is launched only once when the url of the temp file changes
    LaunchedEffect(key1 = viewModel.tempFileUrl) {
        viewModel.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    return CameraLauncherStateCollector(
        pickImageFromAlbumLauncher,
        cameraLauncher,
        permissionLauncher
    )
}