package id.petersam.dhuwite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import id.petersam.dhuwite.model.CategoryEntity
import id.petersam.dhuwite.model.TransactionEntity

@Database(entities = [TransactionEntity::class, CategoryEntity::class], version = 1)
abstract class DhuwitDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}