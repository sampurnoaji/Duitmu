package id.petersam.dhuwite.data

import id.petersam.dhuwite.data.local.TransactionDao
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.model.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val transactionDao: TransactionDao) {

    fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAll().map {
            it.map { trxEntity ->
                trxEntity.toDomain()
            }
        }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }
}