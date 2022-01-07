package id.petersam.dhuwite.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import id.petersam.dhuwite.util.DummyData
import id.petersam.dhuwite.util.PrefConstant
import id.petersam.dhuwite.util.stringSetLiveData
import javax.inject.Inject

class TransactionSharedPreference @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {
    private fun getTransactionExpenseCategory(): MutableSet<String> {
        return sharedPreferences.getStringSet(
            PrefConstant.PREF_EXPENSE_CATEGORIES,
            DummyData.expenseCategories
        ) ?: DummyData.expenseCategories
    }

    private fun getTransactionIncomeCategory(): MutableSet<String> {
        return sharedPreferences.getStringSet(
            PrefConstant.PREF_INCOME_CATEGORIES,
            DummyData.incomeCategories
        ) ?: DummyData.incomeCategories
    }

    fun getTransactionExpenseCategories(): LiveData<Set<String>> {
        return sharedPreferences.stringSetLiveData(
            PrefConstant.PREF_EXPENSE_CATEGORIES,
            DummyData.expenseCategories
        )
    }

    fun getTransactionIncomeCategories(): LiveData<Set<String>> {
        return sharedPreferences.stringSetLiveData(
            PrefConstant.PREF_INCOME_CATEGORIES,
            DummyData.incomeCategories
        )
    }

    fun addTransactionExpenseCategory(category: String) {
        val categories = getTransactionExpenseCategory()
        categories.add(category)
        editor.putStringSet(PrefConstant.PREF_EXPENSE_CATEGORIES, categories)
        editor.apply()
    }

    fun addTransactionIncomeCategory(category: String) {
        val categories = getTransactionIncomeCategory()
        categories.add(category)
        editor.putStringSet(PrefConstant.PREF_INCOME_CATEGORIES, categories)
        editor.apply()
    }

    fun updateTransactionExpenseCategory(oldCategory: String, newCategory: String) {
        val categories = getTransactionExpenseCategory()
        categories.remove(oldCategory)
        categories.add(newCategory)
        editor.putStringSet(PrefConstant.PREF_EXPENSE_CATEGORIES, categories)
        editor.apply()
    }

    fun updateTransactionIncomeCategory(oldCategory: String, newCategory: String) {
        val categories = getTransactionIncomeCategory()
        categories.remove(oldCategory)
        categories.add(newCategory)
        editor.putStringSet(PrefConstant.PREF_INCOME_CATEGORIES, categories)
        editor.apply()
    }

    fun deleteTransactionExpenseCategory(category: String) {
        val categories = getTransactionExpenseCategory()
        categories.remove(category)

        editor.putStringSet(PrefConstant.PREF_EXPENSE_CATEGORIES, categories)
        editor.commit()
    }

    fun deleteTransactionIncomeCategory(category: String) {
        val categories = getTransactionIncomeCategory()
        categories.remove(category)

        editor.putStringSet(PrefConstant.PREF_INCOME_CATEGORIES, categories)
        editor.apply()
    }
}