package id.petersam.dhuwite.ui.create.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.dhuwite.data.TransactionRepository
import id.petersam.dhuwite.model.Category
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.ui.create.CreateTransactionActivity
import id.petersam.dhuwite.util.LoadState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionCategoryViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type> get() = _type

    private val _insertCategory = MutableLiveData<LoadState<Boolean>>()
    val insertCategory: LiveData<LoadState<Boolean>> get() = _insertCategory

    private val _expenseCategories =
        repository.getExpenseCategories().asLiveData().map { categories ->
            categories.map { it.category }.sortedBy { it }
        }
    private val _incomeCategories =
        repository.getIncomeCategories().asLiveData().map { categories ->
            categories.map { it.category }.sortedBy { it }
        }
    val categories = MediatorLiveData<List<String>>().apply {
        addSource(_type) { type ->
            value = if (type == Transaction.Type.EXPENSE) _expenseCategories.value
            else _incomeCategories.value
        }
        addSource(_expenseCategories) {
            if (_type.value == Transaction.Type.EXPENSE) value = it
        }
        addSource(_incomeCategories) {
            if (_type.value == Transaction.Type.INCOME) value = it
        }
    }

    fun onTypeChanged(index: Int) {
        _type.value =
            if (index == CreateTransactionActivity.INCOME_BUTTON_INDEX) Transaction.Type.INCOME
            else Transaction.Type.EXPENSE
    }

    fun insertCategory(category: String, type: Transaction.Type) {
        _insertCategory.value = LoadState.Loading
        viewModelScope.launch {
            try {
                repository.insertCategory(Category(category, type))
                _insertCategory.value = LoadState.Success(true)
            } catch (e: Exception) {
                _insertCategory.value = LoadState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun deleteCategory(category: String) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun updateTransactionExpenseCategory(oldCategory: String, newCategory: String) {
        repository.updateTransactionExpenseCategory(oldCategory, newCategory)
    }

    fun updateTransactionIncomeCategory(oldCategory: String, newCategory: String) {
        repository.updateTransactionIncomeCategory(oldCategory, newCategory)
    }
}