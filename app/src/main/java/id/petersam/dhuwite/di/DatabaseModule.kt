package id.petersam.dhuwite.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.petersam.dhuwite.data.local.DhuwitDatabase
import id.petersam.dhuwite.data.local.TransactionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): DhuwitDatabase {
        return Room.databaseBuilder(context, DhuwitDatabase::class.java, "dhuwit.db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTransactionDao(db: DhuwitDatabase): TransactionDao {
        return db.transactionDao()
    }
}