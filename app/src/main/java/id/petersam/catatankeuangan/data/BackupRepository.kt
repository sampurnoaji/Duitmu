package id.petersam.catatankeuangan.data

import id.petersam.catatankeuangan.data.local.PrefSource
import id.petersam.catatankeuangan.data.local.TransactionDao
import id.petersam.catatankeuangan.model.CategoryEntity
import id.petersam.catatankeuangan.model.TransactionEntity
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val prefSource: PrefSource
) {
    fun setLatestBackupTime(docId: String) {
        prefSource.setLatestBackupTime(docId)
    }

    fun getLatestBackupTime(): String? = prefSource.getLatestBackupTime()

    suspend fun getBackupTransactions(): List<TransactionEntity> {
        return transactionDao.getBackupTransactions()
    }

    suspend fun getBackupCategories(): List<CategoryEntity> {
        return transactionDao.getBackupCategories()
    }

    suspend fun insertBackupTransactions(transactionEntity: TransactionEntity) {
        transactionDao.insertTransaction(transactionEntity)
    }

    suspend fun insertCategory(categoryEntity: CategoryEntity) {
        transactionDao.insertCategory(categoryEntity)
    }
}