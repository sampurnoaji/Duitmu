package id.petersam.dhuwite.ui.create.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.dhuwite.data.TransactionRepository
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.ui.create.CreateTransactionActivity
import javax.inject.Inject

@HiltViewModel
class TransactionCategoryViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val incomeCategories = repository.getIncomeCategories().map { it }.sortedBy { it }
    val expenseCategories = repository.getExpenseCategories().map { it }.sortedBy { it }

    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type> get() = _type

    fun onTypeChanged(index: Int) {
        _type.value =
            if (index == CreateTransactionActivity.INCOME_BUTTON_INDEX) Transaction.Type.INCOME
            else Transaction.Type.EXPENSE
    }

    fun addTransactionExpenseCategory(category: String) {
        repository.addTransactionExpenseCategory(category)
    }

    fun addTransactionIncomeCategory(category: String) {
        repository.addTransactionIncomeCategory(category)
    }
}