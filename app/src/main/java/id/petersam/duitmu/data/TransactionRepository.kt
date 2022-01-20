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

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun getTransaction(trxId: String): Transaction =
        transactionDao.getTransaction(trxId).toDomain()

    fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransaction().map {
            it.map { trxEntity ->
                trxEntity.toDomain()
            }
        }
    }

    suspend fun getSummaryExpenseTransactions(): List<Pair<String, Long>> {
        return transactionDao.getSummaryTransactions(Transaction.Type.EXPENSE.readable).map {
            Pair(
                it.date.toDate(DatePattern.YMD)?.toReadableString(DatePattern.DMY_LONG).orEmpty(),
                it.amount
            )
        }
    }

    suspend fun getSummaryIncomeTransactions(): List<Pair<String, Long>> {
        return transactionDao.getSummaryTransactions(Transaction.Type.INCOME.readable).map {
            Pair(
                it.date.toDate(DatePattern.YMD)?.toReadableString(DatePattern.DMY_LONG).orEmpty(),
                it.amount
            )
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun getIncomeCategoryPercentage(): List<Pair<String, Long>> {
        return transactionDao.getCategoryPercentage(Transaction.Type.INCOME.readable).map {
            Pair(it.category, it.amount)
        }
    }

    suspend fun getExpenseCategoryPercentage(): List<Pair<String, Long>> {
        return transactionDao.getCategoryPercentage(Transaction.Type.EXPENSE.readable).map {
            Pair(it.category, it.amount)
        }
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