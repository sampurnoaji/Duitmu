package id.petersam.dhuwite.data

import androidx.lifecycle.LiveData
import id.petersam.dhuwite.data.local.TransactionDao
import id.petersam.dhuwite.data.local.TransactionSharedPreference
import id.petersam.dhuwite.model.Category
import id.petersam.dhuwite.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionSharedPreference: TransactionSharedPreference
) {

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
        transactionDao.insertCategory(category.toEntity())
    }

    suspend fun deleteCategory(category: String) {
        transactionDao.deleteCategory(category)
    }




    fun getTransactionExpenseCategories(): LiveData<Set<String>> {
        return transactionSharedPreference.getTransactionExpenseCategories()
    }

    fun getTransactionIncomeCategories(): LiveData<Set<String>> {
        return transactionSharedPreference.getTransactionIncomeCategories()
    }

    fun updateTransactionExpenseCategory(oldCategory: String, newCategory: String) {
        transactionSharedPreference.updateTransactionExpenseCategory(oldCategory, newCategory)
    }

    fun updateTransactionIncomeCategory(oldCategory: String, newCategory: String) {
        transactionSharedPreference.updateTransactionIncomeCategory(oldCategory, newCategory)
    }
}