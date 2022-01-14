package id.petersam.duitmu.data

import id.petersam.duitmu.data.local.TransactionDao
import id.petersam.duitmu.model.Category
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.toDate
import id.petersam.duitmu.util.toReadableString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val transactionDao: TransactionDao) {

    fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransaction().map {
            it.map { trxEntity ->
                trxEntity.toDomain()
            }
        }
    }

    suspend fun getSummaryExpenseTransactions(): List<Pair<String, Long>> {
        return transactionDao.getSummaryExpenseTransactions().map { trx ->
            Pair(
                trx.date.toDate(DatePattern.YMD)?.toReadableString(DatePattern.DMY_LONG).orEmpty(),
                trx.amount
            )
        }
    }

    suspend fun getSummaryIncomeTransactions(): List<Pair<String, Long>> {
        return transactionDao.getSummaryIncomeTransactions().map { trx ->
            Pair(
                trx.date.toDate(DatePattern.YMD)?.toReadableString(DatePattern.DMY_LONG).orEmpty(),
                trx.amount
            )
        }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    fun getExpenseCategories(): Flow<List<Category>> {
        return transactionDao.getAllCategory(Transaction.Type.EXPENSE.readable).map {
            it.map { categoryEntity ->
                categoryEntity.toDomain()
            }
        }
    }

    fun getIncomeCategories(): Flow<List<Category>> {
        return transactionDao.getAllCategory(Transaction.Type.INCOME.readable).map {
            it.map { categoryEntity ->
                categoryEntity.toDomain()
            }
        }
    }

    suspend fun insertCategory(category: Category) {
        transactionDao.insertCategory(listOf(category.toEntity()))
    }

    suspend fun deleteCategory(category: Category) {
        transactionDao.deleteCategory(category.toEntity())
    }

    suspend fun updateCategory(category: Category) {
        transactionDao.updateCategory(category.toEntity())
    }
}