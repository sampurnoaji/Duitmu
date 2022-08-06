package id.petersam.catatankeuangan.data.local

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import id.petersam.catatankeuangan.R
import id.petersam.catatankeuangan.model.Category
import id.petersam.catatankeuangan.model.CategoryEntity
import id.petersam.catatankeuangan.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class DatabaseCallback(
    private val context: Context,
    private val provider: Provider<TransactionDao>
) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch(Dispatchers.IO) {
            populateDatabase(context)
        }
    }

    private suspend fun populateDatabase(context: Context) {
        val categories = mutableListOf<CategoryEntity>()
        with(context.resources) {
            categories.addAll(getStringArray(R.array.expense_categories)
                .map { Category(category = it, type = Transaction.Type.EXPENSE).toEntity() })
            categories.addAll(getStringArray(R.array.income_categories)
                .map { Category(category = it, type = Transaction.Type.INCOME).toEntity() })
        }
        provider.get().insertCategories(categories)
    }
}