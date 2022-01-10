package id.petersam.duitmu.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.petersam.duitmu.data.local.DatabaseCallback
import id.petersam.duitmu.data.local.DuitmuDatabase
import id.petersam.duitmu.data.local.TransactionDao
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
    ): DuitmuDatabase {
        return Room.databaseBuilder(context, DuitmuDatabase::class.java, "duitmu.db")
            .addCallback(DatabaseCallback(context, provider))
//            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideTransactionDao(db: DuitmuDatabase): TransactionDao {
        return db.transactionDao()
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {}
    }
}