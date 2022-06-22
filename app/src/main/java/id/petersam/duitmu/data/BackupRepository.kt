package id.petersam.duitmu.data

import id.petersam.duitmu.data.local.PrefSource
import id.petersam.duitmu.data.local.TransactionDao
import id.petersam.duitmu.model.TransactionEntity
import java.util.Date
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val prefSource: PrefSource
) {
    fun setLatestBackupTime(docId: String) {
        prefSource.setLatestBackupTime(docId)
    }

    fun getLatestBackupTime(): String? = prefSource.getLatestBackupTime()

    suspend fun getBackupTransactions(
        startDate: Date? = null,
        endDate: Date? = null
    ): List<TransactionEntity> {
        return transactionDao.getBackupTransactions(startDate, endDate)
    }

    suspend fun insertBackupTransactions(transactionEntity: TransactionEntity) {
        transactionDao.insertTransaction(transactionEntity)
    }
}