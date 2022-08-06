package id.petersam.catatankeuangan.data.google

import android.content.Intent
import id.petersam.catatankeuangan.model.google.User

interface GoogleDrive {
    fun getLatestBackupTime(): String?
    suspend fun checkStatus(): User?
    fun login(): Intent
    suspend fun uploadFile()
    suspend fun downloadFile()
}