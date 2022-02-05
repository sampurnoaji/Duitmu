package id.petersam.duitmu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.petersam.duitmu.model.CategoryEntity
import id.petersam.duitmu.model.TransactionEntity
import id.petersam.duitmu.util.Converters

@Database(entities = [TransactionEntity::class, CategoryEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class DuitmuDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}