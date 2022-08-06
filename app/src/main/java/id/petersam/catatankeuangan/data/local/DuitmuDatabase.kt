package id.petersam.catatankeuangan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.petersam.catatankeuangan.model.CategoryEntity
import id.petersam.catatankeuangan.model.TransactionEntity
import id.petersam.catatankeuangan.util.DateConverters

@Database(entities = [TransactionEntity::class, CategoryEntity::class], version = 1)
@TypeConverters(DateConverters::class)
abstract class DuitmuDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}