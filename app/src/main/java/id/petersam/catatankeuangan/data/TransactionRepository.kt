package id.petersam.catatankeuangan.data

import id.petersam.catatankeuangan.data.local.TransactionDao
import id.petersam.catatankeuangan.model.Category
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.util.DatePattern
import id.petersam.catatankeuangan.util.maximizeTime
import id.petersam.catatankeuangan.util.minimizeTime
import id.petersam.catatankeuangan.util.toReadableString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val transactionDao: TransactionDao) {

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun getTransaction(trxId: String): Transaction =
        transactionDao.getTransaction(trxId).toDomain()

    fun getTransactions(
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<List<Transaction>> {
        return transactionDao.getAllTransaction(startDate?.minimizeTime(), endDate?.maximizeTime())
            .map {
                it.map { trxEntity ->
                    trxEntity.toDomain()
                }
            }
    }

    suspend fun getSummaryExpenseTransactions(
        startDate: Date? = null,
        endDate: Date? = null
    ): List<Pair<String, Long>> {
        return transactionDao.getSummaryTransactions(
            Transaction.Type.EXPENSE.value, startDate?.minimizeTime(), endDate?.maximizeTime()
        ).map {
            Pair(it.date.toReadableString(DatePattern.DMY_LONG), it.amount)
        }
    }

    suspend fun getSummaryIncomeTransactions(
        startDate: Date? = null,
        endDate: Date? = null
    ): List<Pair<String, Long>> {
        return transactionDao.getSummaryTransactions(
            Transaction.Type.INCOME.value, startDate?.minimizeTime(), endDate?.maximizeTime()
        ).map {
            Pair(it.date.toReadableString(DatePattern.DMY_LONG), it.amount)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun getIncomeCategoryPercentage(
        startDate: Date? = null,
        endDate: Date? = null
    ): List<Pair<String, Long>> {
        return transactionDao.getCategoryPercentage(
            Transaction.Type.INCOME.value, startDate?.minimizeTime(), endDate?.maximizeTime()
        ).map {
            Pair(it.category, it.amount)
        }
    }

    suspend fun getExpenseCategoryPercentage(
        startDate: Date? = null,
        endDate: Date? = null
    ): List<Pair<String, Long>> {
        return transactionDao.getCategoryPercentage(
            Transaction.Type.EXPENSE.value, startDate?.minimizeTime(), endDate?.maximizeTime()
        ).map {
            Pair(it.category, it.amount)
        }
    }

    fun getExpenseCategories(): Flow<List<Category>> {
        return transactionDao.getAllCategory(Transaction.Type.EXPENSE.value).map {
            it.map { categoryEntity ->
                categoryEntity.toDomain()
            }
        }
    }

    fun getIncomeCategories(): Flow<List<Category>> {
        return transactionDao.getAllCategory(Transaction.Type.INCOME.value).map {
            it.map { categoryEntity ->
                categoryEntity.toDomain()
            }
        }
    }

    suspend fun insertCategory(category: Category) {
        transactionDao.insertCategories(listOf(category.toEntity()))
    }

    suspend fun deleteCategory(category: Category) {
        transactionDao.deleteCategory(category.toEntity())
    }

    suspend fun updateCategory(category: Category) {
        transactionDao.updateCategory(category.toEntity())
    }
}