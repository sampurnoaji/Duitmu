package id.petersam.dhuwite.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import id.petersam.dhuwite.model.CategoryEntity
import id.petersam.dhuwite.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransaction(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM CategoryEntity WHERE type = :type")
    fun getAllCategory(type: String): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Query("DELETE FROM CategoryEntity WHERE category = :id")
    suspend fun deleteCategory(id: String)
}