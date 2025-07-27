@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.backupManager

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.constants.saveToDownloadFolderComposable
import com.example.recipebook.constants.selectFileComposable
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.FoldablePreview
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.RecipeBookTheme

object BackupManagerDestination : NavigationDestinationNoParams {
    override val route = "backup_manager"
    override val titleRes = R.string.routeTitle_recipeManager
}


@Composable
fun BackupManagerScreen(
    screenSize: ScreenSize,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    backupManagerViewModel: BackupManagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val localContext = LocalContext.current

    val downloadDb = saveToDownloadFolderComposable(
        localContext,
        stringResource(R.string.download_backup_success)
    ) {
        backupManagerViewModel.getDbDownloadFile(localContext)
    }

    val chooseFileToUpload = selectFileComposable(localContext) { selectedFileUri, context ->
        backupManagerViewModel.loadDbFile(selectedFileUri, context)

        // displaying a toast message
        Toast.makeText(context, R.string.upload_backup_success, Toast.LENGTH_SHORT).show()
    }

    /*
        The app crashes when returning to home screen
        using the default back button after a DB upload.
        This is caused by the lingering reference to the previous
        DB inside the home page.

        To avoid it, the default back behaviour (navigateBack)
        is replaced with an explicit navigation to the home page,
        thus forcing a reload of the page and the DB connection.
    */
    BackHandler(
        enabled = true
    ) {
        onNavigateUp()
    }

    BackupManagerScreenStateCollector(
        screenSize = screenSize,
        onNavigateUp = onNavigateUp,
        canNavigateBack = canNavigateBack,
        downloadDb = downloadDb,
        chooseFileToUpload = chooseFileToUpload
    )
}

@Composable
private fun BackupManagerScreenStateCollector(
    screenSize: ScreenSize,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    downloadDb: () -> Unit,
    chooseFileToUpload: () -> Unit
) {

    Scaffold(
        topBar = {
            RecipeBookTopAppBar(
                title = stringResource(BackupManagerDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {

            Button(
                onClick = downloadDb
            ) {
                Text(stringResource(R.string.download_backup_button_name))
            }

            Button(
                onClick = chooseFileToUpload
            ) {
                Text(stringResource(R.string.upload_backup_button_name))
            }
        }
    }
}

//region Preview

@PhonePreview
@Composable
fun HomeScreenPhonePreview() {
    RecipeBookTheme {
        BackupManagerScreenStateCollector(
            ScreenSize.SMALL,
            onNavigateUp = {},
            canNavigateBack = true,
            downloadDb = {},
            chooseFileToUpload = {}
        )
    }
}

@FoldablePreview
@Composable
fun HomeScreenFoldablePreview() {
    RecipeBookTheme {
        BackupManagerScreenStateCollector(
            ScreenSize.SMALL,
            onNavigateUp = {},
            canNavigateBack = true,
            downloadDb = {},
            chooseFileToUpload = {}
        )
    }
}

@TabletPreview
@Composable
fun HomeScreenTabletPreview() {
    RecipeBookTheme {
        BackupManagerScreenStateCollector(
            ScreenSize.SMALL,
            onNavigateUp = {},
            canNavigateBack = true,
            downloadDb = {},
            chooseFileToUpload = {}
        )
    }
}

//endregion