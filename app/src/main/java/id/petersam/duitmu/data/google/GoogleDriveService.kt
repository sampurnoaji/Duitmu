package id.petersam.duitmu.data.google

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.Scopes
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import id.petersam.duitmu.R
import id.petersam.duitmu.data.BackupRepository
import id.petersam.duitmu.model.CategoryEntity
import id.petersam.duitmu.model.TransactionEntity
import id.petersam.duitmu.model.google.User
import id.petersam.duitmu.util.fromJson
import id.petersam.duitmu.util.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.Collections
import javax.inject.Inject
import com.google.api.services.drive.model.File as GoogleFile


class GoogleDriveService @Inject constructor(
    private val context: Context,
    private val googleSignInClient: GoogleSignInClient,
    private val repository: BackupRepository
) : GoogleDrive {

    private var drive: Drive? = null

    override fun getLatestBackupTime(): String? = repository.getLatestBackupTime()

    override suspend fun checkStatus(): User? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return if (account != null) {
            drive = provideDrive()
            User(
                name = account.displayName.orEmpty(),
                email = account.email.orEmpty(),
                profilePicture = account.photoUrl
            )
        } else null
    }

    override fun login(): Intent = googleSignInClient.signInIntent

    override suspend fun uploadFile() {
        drive?.let { drive ->
            withContext(Dispatchers.IO) {
                val fileIds = getToBeDeleteFileIds(drive)

                val trxJson = repository.getBackupTransactions().toJson()
                val trxFile = uploadContent(trxJson, Backup.getTransactionFilename(context), drive)
                repository.setLatestBackupTime(trxFile.createdTime.toStringRfc3339())

                val categoriesJson = repository.getBackupCategories().toJson()
                uploadContent(categoriesJson, Backup.getCategoryFilename(context), drive)

                for (id in fileIds) {
                    drive.files().delete(id).execute()
                }
            }
        }
    }

    override suspend fun downloadFile() {
        drive?.let { drive ->
            withContext(Dispatchers.IO) {
                val result = drive.files().list()
                    .setQ("mimeType='application/json'")
                    .execute()

                val trxJson = downloadContent(result, Backup.getTransactionFilename(context), drive)
                val trxs = trxJson?.fromJson<TransactionEntity>()
                trxs?.forEach {
                    repository.insertBackupTransactions(it)
                }

                val categoriesJson =
                    downloadContent(result, Backup.getCategoryFilename(context), drive)
                val categories = categoriesJson?.fromJson<CategoryEntity>()
                categories?.forEach {
                    repository.insertCategory(it)
                }
            }
        }
    }

    private fun uploadContent(json: String, filename: String, drive: Drive): GoogleFile {
        val file = File.createTempFile(filename, null, context.cacheDir).apply {
            writeText(json)
        }
        val gFileMetadata = GoogleFile().apply {
            name = filename
        }
        val gFileContent = FileContent("", file)
        val gFile = drive.files()
            .create(gFileMetadata, gFileContent)
            .setFields("id, createdTime")
            .execute()
        return gFile
    }

    private fun downloadContent(result: FileList, filename: String, drive: Drive): String? {
        for (file in result.files) {
            if (file.name == filename) {
                val outputStream: OutputStream = ByteArrayOutputStream()
                drive.files().get(file.id)
                    .executeMediaAndDownloadTo(outputStream)
                return outputStream.toString()
            }
        }
        return null
    }

    private fun getToBeDeleteFileIds(drive: Drive): List<String> {
        val ids = mutableListOf<String>()
        val result = drive.files().list()
            .setQ("mimeType='application/json'")
            .execute()
        for (file in result.files) {
            if (file.name == Backup.getTransactionFilename(context)
                || file.name == Backup.getCategoryFilename(context)
            ) {
                ids.add(file.id)
            }
        }
        return ids
    }

    private fun provideDrive(): Drive {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(Scopes.DRIVE_FILE)
        )
        credential.selectedAccount = googleSignInAccount?.account
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        ).setApplicationName(context.resources.getString(R.string.app_name)).build()
    }
}