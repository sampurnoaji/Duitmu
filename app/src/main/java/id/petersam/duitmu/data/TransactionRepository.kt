package id.petersam.duitmu.data

import id.petersam.duitmu.data.local.TransactionDao
import id.petersam.duitmu.model.Category
import id.petersam.duitmu.model.Transaction
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