package id.petersam.duitmu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import id.petersam.duitmu.model.CategoryEntity
import id.petersam.duitmu.model.TransactionEntity

@Database(entities = [TransactionEntity::class, CategoryEntity::class], version = 1)
abstract class DuitmuDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}