package id.petersam.dhuwite.data.local

import androidx.room.Dao
import androidx.room.Query
import id.petersam.dhuwite.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM TransactionEntity")
    fun getAll(): Flow<List<TransactionEntity>>
}