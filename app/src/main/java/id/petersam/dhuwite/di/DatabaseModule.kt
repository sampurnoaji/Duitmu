package id.petersam.dhuwite.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.petersam.dhuwite.data.local.DatabaseCallback
import id.petersam.dhuwite.data.local.DhuwitDatabase
import id.petersam.dhuwite.data.local.TransactionDao
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context,
        provider: Provider<TransactionDao>
    ): DhuwitDatabase {
        return Room.databaseBuilder(context, DhuwitDatabase::class.java, "dhuwit.db")
            .addCallback(DatabaseCallback(context, provider))
//            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideTransactionDao(db: DhuwitDatabase): TransactionDao {
        return db.transactionDao()
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {}
    }
}