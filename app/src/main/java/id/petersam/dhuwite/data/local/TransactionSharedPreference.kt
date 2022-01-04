package id.petersam.dhuwite.data.local

import android.content.SharedPreferences
import id.petersam.dhuwite.util.PrefConstant
import javax.inject.Inject

class TransactionSharedPreference @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {
    fun getTransactionIncomeCategory(): Set<String> {
        return sharedPreferences.getStringSet(PrefConstant.PREF_INCOME_CATEGORIES, incomeCategories)
            ?: incomeCategories
    }

    fun getTransactionExpenseCategory(): Set<String> {
        return sharedPreferences.getStringSet(PrefConstant.PREF_INCOME_CATEGORIES, expenseCategories)
            ?: expenseCategories
    }

    private val incomeCategories = setOf(
        "Gaji", "Deposito", "Hadiah"
    )

    private val expenseCategories = setOf(
        "Belanja", "Listrik", "Internet"
    )
}