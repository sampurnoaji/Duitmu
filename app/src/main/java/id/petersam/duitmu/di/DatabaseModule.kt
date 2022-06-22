package id.petersam.duitmu.di

import android.content.Context
import android.content.SharedPreferences
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

    const val DB_NAME = "duitmu.db"
    private const val PREF_NAME = "duitmu.pref"


    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context,
        provider: Provider<TransactionDao>
    ): DuitmuDatabase {
        return Room.databaseBuilder(context, DuitmuDatabase::class.java, DB_NAME)
            .addCallback(DatabaseCallback(context, provider))
//            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun provideTransactionDao(db: DuitmuDatabase): TransactionDao {
        return db.transactionDao()
    }
    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSharedPrefEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor =
        sharedPreferences.edit()

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {}
    }
}