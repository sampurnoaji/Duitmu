package id.petersam.catatankeuangan.data.firebase

import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val dataSource: FirebaseDataSource){

    fun getBooleanRemoteConfigValue(key: String) = dataSource.getBooleanRemoteConfigValue(key)
}