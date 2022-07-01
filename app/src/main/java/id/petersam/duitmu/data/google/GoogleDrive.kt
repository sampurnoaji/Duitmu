package id.petersam.duitmu.data.google

import android.content.Intent
import id.petersam.duitmu.model.google.User

interface GoogleDrive {
    fun getLatestBackupTime(): String?
    suspend fun checkStatus(): User?
    fun login(): Intent
    suspend fun uploadFile()
    suspend fun downloadFile()
}