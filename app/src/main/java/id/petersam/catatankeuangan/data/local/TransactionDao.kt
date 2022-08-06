package id.petersam.catatankeuangan.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.petersam.catatankeuangan.model.CategoryEntity
import id.petersam.catatankeuangan.model.TransactionEntity
import id.petersam.catatankeuangan.model.entity.CategoryChartEntity
import id.petersam.catatankeuangan.model.entity.TransactionChartEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity WHERE createdAt = :trxId")
    suspend fun getTransaction(trxId: String): TransactionEntity

    @Query(
        """SELECT * FROM TransactionEntity
            WHERE (:startDate IS NULL OR date >= :startDate)
            AND (:endDate IS NULL OR date <= :endDate)
            ORDER BY date DESC"""
    )
    fun getAllTransaction(
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<List<TransactionEntity>>

    @Query(
        """SELECT date, SUM(amount) as amount FROM TransactionEntity 
            WHERE type = :type
            AND (:startDate IS NULL OR date >= :startDate)
            AND (:endDate IS NULL OR date <= :endDate)
            GROUP BY date"""
    )
    suspend fun getSummaryTransactions(
        type: String,
        startDate: Date? = null,
        endDate: Date? = null
    ): List<TransactionChartEntity>

    @Query(
        """SELECT category, SUM(amount) as amount FROM TransactionEntity 
            WHERE type = :type
            AND (:startDate IS NULL OR date >= :startDate)
            AND (:endDate IS NULL OR date <= :endDate)
            GROUP BY category"""
    )
    suspend fun getCategoryPercentage(
        type: String,
        startDate: Date? = null,
        endDate: Date? = null
    ): List<CategoryChartEntity>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query(
        "SELECT * FROM CategoryEntity " +
                "WHERE (:type IS NULL OR type = :type) " +
                "ORDER BY category ASC"
    )
    fun getAllCategory(type: String? = null): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categories: CategoryEntity)

    @Insert
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("SELECT * FROM TransactionEntity")
    suspend fun getBackupTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM CategoryEntity")
    suspend fun getBackupCategories(): List<CategoryEntity>
}