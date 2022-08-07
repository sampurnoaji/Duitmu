package id.petersam.catatankeuangan.data.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject

class FirebaseDataSource @Inject constructor() {

    private val remoteConfig = Firebase.remoteConfig

    init {
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        remoteConfig.apply {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3_600
            }
            setConfigSettingsAsync(configSettings)
            fetchAndActivate()
                .addOnCompleteListener {

                }
        }
    }

    fun getBooleanRemoteConfigValue(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }
}