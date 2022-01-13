package id.petersam.duitmu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import id.petersam.duitmu.model.CategoryEntity
import id.petersam.duitmu.model.TransactionEntity
import id.petersam.duitmu.model.entity.TransactionChartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity ORDER BY date DESC")
    fun getAllTransaction(): Flow<List<TransactionEntity>>

    @Query("SELECT date, SUM(amount) as amount FROM TransactionEntity WHERE type LIKE 'expense' GROUP BY date")
    suspend fun getSummaryExpenseTransactions(): List<TransactionChartEntity>

    @Query("SELECT date, SUM(amount) as amount FROM TransactionEntity WHERE type LIKE 'income' GROUP BY date")
    suspend fun getSummaryIncomeTransactions(): List<TransactionChartEntity>

    @Query("SELECT * FROM CategoryEntity WHERE type = :type ORDER BY category ASC")
    fun getAllCategory(type: String): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)
}