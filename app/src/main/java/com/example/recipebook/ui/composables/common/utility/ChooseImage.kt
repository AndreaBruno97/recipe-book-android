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
import com.example.recipebook.constants.FileConstants
import com.example.recipebook.constants.FileFunctions
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
    return "${FileConstants.RECIPES_FOLDER}/${recipeId.toHexString()}"
}

fun getRecipeImage(recipeId: ObjectId, context: Context): File? {
    var recipeImageFile: File? = null
    val recipeFolderPath = getRecipeFolderPath(recipeId)
    val recipeFolder = File(context.filesDir, recipeFolderPath)

    if (recipeFolder.exists() && recipeFolder.isDirectory) {
        recipeImageFile = recipeFolder.listFiles()?.firstOrNull { file ->
            file.exists() &&
                    file.isFile &&
                    file.name.startsWith(FileConstants.RECIPE_IMAGE_FILE_NAME_PREFIX)
        }
    }

    return recipeImageFile
}

fun loadRecipeImage(recipeId: ObjectId, context: Context): ImageBitmap? {
    var recipeImage: ImageBitmap? = null
    val recipeImageFile = getRecipeImage(recipeId, context)

    if (recipeImageFile != null && recipeImageFile.isFile) {
        val bitmapOptions = BitmapFactory.Options()
        val recipeBytes = recipeImageFile.readBytes()
        val bitmap: Bitmap =
            BitmapFactory.decodeByteArray(recipeBytes, 0, recipeBytes.size, bitmapOptions)
        recipeImage = bitmap.asImageBitmap()
    }

    return recipeImage
}

suspend fun saveRecipeImage(
    folderPath: String,
    sourceFilePath: String?,
    context: Context
) {
    val folderFile = File(context.filesDir, folderPath)

    // Create directory if it doesn't exist
    folderFile.mkdirs()

    // Clear directory in case previous file exists
    folderFile.listFiles()?.forEach { it.delete() }

    // Create new file
    if (sourceFilePath != null) {
        val fileBytes = File(sourceFilePath).readBytes()
        withContext(Dispatchers.IO) {
            val imageFile = FileFunctions.getFileNameWithDate(
                FileConstants.RECIPE_IMAGE_FILE_NAME_PREFIX,
                FileConstants.RECIPE_IMAGE_FILE_NAME_SUFFIX,
                folderFile
            )

            FileOutputStream(imageFile).use {
                it.write(fileBytes)
            }
        }
    }
}

open class ImageManagerViewModel : ViewModel() {

    var tempFileUrl: Uri? by mutableStateOf(null)
    var tempFileUrlDirectString: String? by mutableStateOf(null)
    var tempImage: ImageBitmap? by mutableStateOf(null)

    fun clearImage() {
        tempFileUrl = null
        tempFileUrlDirectString = null
        tempImage = null
    }

    fun onReceive(
        intent: Intent,
        retrieveImage: (ImageBitmap?, String?) -> Unit
    ) = runBlocking {
        viewModelScope.launch(coroutineContext) {
            when (intent) {
                is Intent.OnPermissionGrantedWith -> {
                    // Create an empty image file in the app's cache directory
                    val tempFile = File.createTempFile(
                        FileConstants.TEMP_IMAGE_PREFIX, /* prefix */
                        FileConstants.TEMP_IMAGE_SUFFIX, /* suffix */
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
                                FileConstants.TEMP_IMAGE_PREFIX, /* prefix */
                                FileConstants.TEMP_IMAGE_SUFFIX, /* suffix */
                                intent.compositionContext.cacheDir  /* cache directory */
                            )
                            tempFilePicked.writeBytes(bytes)
                            tempFileUrlDirectString = tempFilePicked.path

                            retrieveImage(tempImage, tempFileUrlDirectString)
                        } else {
                            // error reading the bytes from the image url
                            retrieveImage(null, null)
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
                    }

                    retrieveImage(tempImage, tempFileUrlDirectString)

                    tempFileUrl = null
                }

                is Intent.OnImageSavingCanceled -> {
                    retrieveImage(null, null)

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
    viewModel: ImageManagerViewModel,
    retrieveImage: (ImageBitmap?, String?) -> Unit
): CameraLauncherStateCollector {
    // launches photo picker
    val pickImageFromAlbumLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { urls ->
            viewModel.onReceive(
                Intent.OnFinishPickingImagesWith(currentContext, urls),
                retrieveImage
            )
        }

    // launches camera
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
            if (isImageSaved) {
                viewModel.onReceive(
                    Intent.OnImageSavedWith(currentContext),
                    retrieveImage
                )
            } else {
                // handle image saving error or cancellation
                viewModel.onReceive(
                    Intent.OnImageSavingCanceled,
                    retrieveImage
                )
            }
        }

    // launches camera permissions
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                viewModel.onReceive(
                    Intent.OnPermissionGrantedWith(currentContext),
                    retrieveImage
                )
            } else {
                // handle permission denied such as:
                viewModel.onReceive(
                    Intent.OnPermissionDenied,
                    retrieveImage
                )
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