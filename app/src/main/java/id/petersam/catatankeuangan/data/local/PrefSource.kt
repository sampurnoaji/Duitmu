package id.petersam.catatankeuangan.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class PrefSource @Inject constructor(
    private val pref: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    companion object {
        private const val LATEST_BACKUP_TIME = "latest_backup_time"
    }

    fun setLatestBackupTime(time: String) {
        editor.putString(LATEST_BACKUP_TIME, time)
        editor.apply()
    }

    fun getLatestBackupTime(): String? = pref.getString(LATEST_BACKUP_TIME, null)
}