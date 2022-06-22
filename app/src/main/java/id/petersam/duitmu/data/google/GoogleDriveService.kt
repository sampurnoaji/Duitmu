package id.petersam.duitmu.data.google

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.Scopes
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import id.petersam.duitmu.R
import id.petersam.duitmu.data.BackupRepository
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
                val json = repository.getBackupTransactions().toJson()
                val file =
                    File.createTempFile(Backup.TRANSACTION_FILENAME, null, context.cacheDir).apply {
                        writeText(json)
                    }
                val gFileMetadata = GoogleFile().apply {
                    name = Backup.TRANSACTION_FILENAME
                }
                val gFileContent = FileContent("", file)
                val gFile = drive.files()
                    .create(gFileMetadata, gFileContent)
                    .setFields("id, createdTime")
                    .execute()
                repository.setLatestBackupTime(gFile.createdTime.toStringRfc3339())
            }
        }
    }

    override suspend fun downloadFile() {
        drive?.let { drive ->
            withContext(Dispatchers.IO) {
                val result = drive.files().list()
                    .setQ("mimeType='application/json'")
                    .execute()
                for (file in result.files) {
                    if (file.name == Backup.TRANSACTION_FILENAME) {
                        val outputStream: OutputStream = ByteArrayOutputStream()
                        drive.files().get(file.id)
                            .executeMediaAndDownloadTo(outputStream)

                        val json = outputStream.toString()
                        val trxs = json.fromJson<TransactionEntity>()
                        trxs?.forEach {
                            repository.insertBackupTransactions(it)
                        }
                    }
                }
            }
        }
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