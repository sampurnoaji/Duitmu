package id.petersam.dhuwite.ui.create.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.dhuwite.data.TransactionRepository
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.ui.create.CreateTransactionActivity
import javax.inject.Inject

@HiltViewModel
class TransactionCategoryViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type> get() = _type

    private val incomeCategories = repository.getTransactionIncomeCategories().map { set ->
        set.toList().sortedBy { it }
    }
    private val expenseCategories = repository.getTransactionExpenseCategories().map { set ->
        set.toList().sortedBy { it }
    }
    val categories = MediatorLiveData<List<String>>().apply {
        addSource(_type) {
            value = if (_type.value == Transaction.Type.EXPENSE) expenseCategories.value
            else incomeCategories.value
        }
        addSource(expenseCategories) {
            if (_type.value == Transaction.Type.EXPENSE) value = it
        }
        addSource(incomeCategories) {
            if (_type.value == Transaction.Type.INCOME) value = it
        }
    }

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

    fun updateTransactionExpenseCategory(oldCategory: String, newCategory: String) {
        repository.updateTransactionExpenseCategory(oldCategory, newCategory)
    }

    fun updateTransactionIncomeCategory(oldCategory: String, newCategory: String) {
        repository.updateTransactionIncomeCategory(oldCategory, newCategory)
    }
}