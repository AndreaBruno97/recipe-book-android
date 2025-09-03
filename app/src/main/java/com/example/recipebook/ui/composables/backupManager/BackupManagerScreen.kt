@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recipebook.ui.composables.backupManager

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.R
import com.example.recipebook.RecipeBookTopAppBar
import com.example.recipebook.constants.DateFunctions
import com.example.recipebook.constants.saveToDownloadFolderComposable
import com.example.recipebook.constants.selectFileComposable
import com.example.recipebook.ui.AppViewModelProvider
import com.example.recipebook.ui.composables.common.utility.TextWithIcon
import com.example.recipebook.ui.composables.common.utility.WarningConfirmationDialog
import com.example.recipebook.ui.composables.common.utility.WarningDialogType
import com.example.recipebook.ui.navigation.NavigationDestinationNoParams
import com.example.recipebook.ui.navigation.ScreenSize
import com.example.recipebook.ui.preview.FoldablePreview
import com.example.recipebook.ui.preview.PhonePreview
import com.example.recipebook.ui.preview.TabletPreview
import com.example.recipebook.ui.theme.BackupManager_Warning
import com.example.recipebook.ui.theme.RecipeBookTheme
import java.util.Date

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

    val backupManagerUiState = backupManagerViewModel.backupManagerUiState

    LaunchedEffect(backupManagerUiState.loadLastBackupDate) {
        if (backupManagerUiState.loadLastBackupDate == true) {
            backupManagerViewModel.getLastLocalBackupDate(localContext)
        }
    }

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

    val uploadLocalBackupSuccessMessage = stringResource(R.string.update_local_backup_success)

    BackupManagerScreenStateCollector(
        screenSize = screenSize,
        onNavigateUp = onNavigateUp,
        canNavigateBack = canNavigateBack,
        lastLocalBackupDate = backupManagerUiState.lastBackupDate,
        isLoadLocalBackupConfirmOpen = backupManagerViewModel.isLoadLocalBackupConfirmOpen,
        downloadDb = downloadDb,
        chooseFileToUpload = chooseFileToUpload,
        updateLocalBackup = {
            backupManagerViewModel.updateLocalBackup(
                uploadLocalBackupSuccessMessage,
                localContext
            )
        },
        uploadFromLocalBackup = {
            backupManagerViewModel.loadLocalBackup(
                uploadLocalBackupSuccessMessage,
                localContext
            )
        },
        openLoadLocalBackupConfirmSection = backupManagerViewModel::openLoadLocalBackupConfirmSection,
        closeLoadLocalBackupConfirmSection = backupManagerViewModel::closeLoadLocalBackupConfirmSection
    )
}

@Composable
private fun BackupManagerScreenStateCollector(
    screenSize: ScreenSize,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    lastLocalBackupDate: Long? = null,
    isLoadLocalBackupConfirmOpen: Boolean = false,
    downloadDb: () -> Unit,
    chooseFileToUpload: () -> Unit,
    updateLocalBackup: () -> Unit,
    uploadFromLocalBackup: () -> Unit,
    openLoadLocalBackupConfirmSection: () -> Unit,
    closeLoadLocalBackupConfirmSection: () -> Unit
) {
    val isLocalBackupFilePresent = lastLocalBackupDate != null

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
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            BackupLastSaveDateSection(
                lastLocalBackupDate = lastLocalBackupDate
            )

            BackupSaveSection(
                downloadDb = downloadDb,
                updateLocalBackup = updateLocalBackup
            )

            BackupLoadSection(
                isLocalBackupFilePresent = isLocalBackupFilePresent,
                isLoadLocalBackupConfirmOpen = isLoadLocalBackupConfirmOpen,
                chooseFileToUpload = chooseFileToUpload,
                uploadFromLocalBackup = uploadFromLocalBackup,
                openLoadLocalBackupConfirmSection = openLoadLocalBackupConfirmSection,
                closeLoadLocalBackupConfirmSection = closeLoadLocalBackupConfirmSection
            )
        }
    }
}

@Composable
private fun BackupLastSaveDateSection(
    modifier: Modifier = Modifier,
    lastLocalBackupDate: Long? = null
) {
    val messageString = if (lastLocalBackupDate != null) {
        val lastLocalBackupDateString =
            DateFunctions.getLocaleDateStringFromLong(lastLocalBackupDate, "d MMMM yyyy HH:mm:ss")
        stringResource(
            R.string.backupManager_last_local_backup_date_label,
            lastLocalBackupDateString
        )
    } else {
        stringResource(R.string.backupManager_no_local_backup_date_label)
    }

    TextWithIcon(
        text = messageString,
        icon = Icons.Default.Lock,
        modifier = modifier
    )
}

@Composable
private fun BackupSaveSection(
    modifier: Modifier = Modifier,
    downloadDb: () -> Unit,
    updateLocalBackup: () -> Unit,
) {
    ButtonSection(
        modifier = modifier,
        sectionTitle = stringResource(R.string.backupManager_save_section_title),
        firstButtonDescription = stringResource(R.string.backupManager_download_backup_button_description),
        firstButtonName = stringResource(R.string.backupManager_download_backup_button_name),
        firstButtonAction = downloadDb,
        secondButtonDescription = stringResource(R.string.backupManager_update_local_backup_button_description),
        secondButtonName = stringResource(R.string.backupManager_update_local_backup_button_name),
        secondButtonAction = updateLocalBackup
    )
}

@Composable
private fun BackupLoadSection(
    isLocalBackupFilePresent: Boolean,
    isLoadLocalBackupConfirmOpen: Boolean = false,
    modifier: Modifier = Modifier,
    chooseFileToUpload: () -> Unit,
    uploadFromLocalBackup: () -> Unit,
    openLoadLocalBackupConfirmSection: () -> Unit,
    closeLoadLocalBackupConfirmSection: () -> Unit
) {
    ButtonSection(
        modifier = modifier,
        warningMessage = stringResource(R.string.backupManager_load_section_warning),
        sectionTitle = stringResource(R.string.backupManager_load_section_title),
        firstButtonDescription = stringResource(R.string.backupManager_upload_backup_button_description),
        firstButtonName = stringResource(R.string.backupManager_upload_backup_button_name),
        firstButtonAction = chooseFileToUpload,
        secondButtonDescription = stringResource(R.string.backupManager_upload_from_local_backup_button_desccription),
        secondButtonName = stringResource(R.string.backupManager_upload_from_local_backup_button_name),
        isSecondButtonEnabled = isLocalBackupFilePresent,
        secondButtonAction = openLoadLocalBackupConfirmSection
    )

    WarningConfirmationDialog(
        isPopupOpen = isLoadLocalBackupConfirmOpen,
        text = stringResource(R.string.backupManager_upload_from_local_backup_button_warning_text),
        warningType = WarningDialogType.OVERRIDE,
        onWarningConfirm = {
            uploadFromLocalBackup()
            closeLoadLocalBackupConfirmSection()
        },
        onWarningCancel = closeLoadLocalBackupConfirmSection
    )
}


@Composable
private fun ButtonSection(
    modifier: Modifier = Modifier,
    warningMessage: String = "",
    sectionTitle: String,
    firstButtonDescription: String,
    firstButtonName: String,
    isFirstButtonEnabled: Boolean = true,
    firstButtonAction: () -> Unit,
    secondButtonDescription: String,
    secondButtonName: String,
    isSecondButtonEnabled: Boolean = true,
    secondButtonAction: () -> Unit,
) {
    val isWarningCard = warningMessage.isNotBlank()
    val cardBorder = if (isWarningCard) {
        BorderStroke(
            dimensionResource(R.dimen.card_border),
            MaterialTheme.colorScheme.onErrorContainer
        )
    } else {
        CardDefaults.outlinedCardBorder()
    }

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(),
        border = cardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(sectionTitle)

            if (isWarningCard) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    Icon(
                        imageVector = BackupManager_Warning,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )

                    Text(
                        text = warningMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Column {
                Text(firstButtonDescription)
                Button(
                    onClick = firstButtonAction,
                    enabled = isFirstButtonEnabled
                ) {
                    Text(firstButtonName)
                }
            }

            Column {
                Text(secondButtonDescription)
                Button(
                    onClick = secondButtonAction,
                    enabled = isSecondButtonEnabled
                ) {
                    Text(secondButtonName)
                }
            }
        }
    }
}

//region Preview

@PhonePreview
@Composable
private fun HomeScreenPhonePreview() {
    RecipeBookTheme {
        BackupManagerScreenStateCollector(
            ScreenSize.SMALL,
            onNavigateUp = {},
            canNavigateBack = true,
            downloadDb = {},
            chooseFileToUpload = {},
            updateLocalBackup = {},
            uploadFromLocalBackup = {},
            openLoadLocalBackupConfirmSection = {},
            closeLoadLocalBackupConfirmSection = {}
        )
    }
}

@FoldablePreview
@Composable
private fun HomeScreenFoldablePreview() {
    RecipeBookTheme {
        BackupManagerScreenStateCollector(
            ScreenSize.SMALL,
            onNavigateUp = {},
            canNavigateBack = true,
            downloadDb = {},
            chooseFileToUpload = {},
            updateLocalBackup = {},
            uploadFromLocalBackup = {},
            openLoadLocalBackupConfirmSection = {},
            closeLoadLocalBackupConfirmSection = {}
        )
    }
}

@TabletPreview
@Composable
private fun HomeScreenTabletPreview() {
    RecipeBookTheme {
        BackupManagerScreenStateCollector(
            ScreenSize.SMALL,
            onNavigateUp = {},
            canNavigateBack = true,
            downloadDb = {},
            chooseFileToUpload = {},
            updateLocalBackup = {},
            uploadFromLocalBackup = {},
            openLoadLocalBackupConfirmSection = {},
            closeLoadLocalBackupConfirmSection = {}
        )
    }
}

@PhonePreview
@Composable
private fun BackupLastSaveDateSectionPreview() {
    RecipeBookTheme {
        BackupLastSaveDateSection(
            lastLocalBackupDate = Date().time
        )
    }
}

@PhonePreview
@Composable
private fun BackupLastSaveDateSectionNoDatePreview() {
    RecipeBookTheme {
        BackupLastSaveDateSection(
            lastLocalBackupDate = null
        )
    }
}

@PhonePreview
@Composable
private fun BackupSaveSectionPreview() {
    RecipeBookTheme {
        BackupSaveSection(
            downloadDb = {},
            updateLocalBackup = {}
        )
    }
}

@PhonePreview
@Composable
private fun BackupLoadSectionPreview() {
    RecipeBookTheme {
        BackupLoadSection(
            isLocalBackupFilePresent = true,
            chooseFileToUpload = {},
            uploadFromLocalBackup = {},
            openLoadLocalBackupConfirmSection = {},
            closeLoadLocalBackupConfirmSection = {}
        )
    }
}

@PhonePreview
@Composable
private fun BackupLoadSectionNoDatePreview() {
    RecipeBookTheme {
        BackupLoadSection(
            isLocalBackupFilePresent = false,
            chooseFileToUpload = {},
            uploadFromLocalBackup = {},
            openLoadLocalBackupConfirmSection = {},
            closeLoadLocalBackupConfirmSection = {}
        )
    }
}

@PhonePreview
@Composable
private fun ButtonSectionPreview() {
    RecipeBookTheme {
        ButtonSection(
            sectionTitle = "Title",
            firstButtonDescription = "First description",
            firstButtonName = "First button",
            firstButtonAction = {},
            secondButtonDescription = "Second description",
            secondButtonName = "Second button",
            secondButtonAction = {}
        )
    }
}

//endregion