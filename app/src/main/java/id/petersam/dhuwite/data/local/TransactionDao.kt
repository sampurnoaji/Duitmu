package id.petersam.dhuwite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import id.petersam.dhuwite.model.CategoryEntity
import id.petersam.dhuwite.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransaction(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM CategoryEntity")
    fun getAllCategory(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)
}