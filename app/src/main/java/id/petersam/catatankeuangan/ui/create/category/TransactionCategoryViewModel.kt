package id.petersam.catatankeuangan.ui.create.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.catatankeuangan.data.TransactionRepository
import id.petersam.catatankeuangan.model.Category
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.ui.create.CreateTransactionActivity
import id.petersam.catatankeuangan.util.LoadState
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

    private val _expenseCategories = repository.getExpenseCategories().asLiveData()
    private val _incomeCategories = repository.getIncomeCategories().asLiveData()
    val categories = MediatorLiveData<List<Category>>().apply {
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
                repository.insertCategory(Category(category = category, type = type))
                _insertCategory.value = LoadState.Success(true)
            } catch (e: Exception) {
                _insertCategory.value = LoadState.Error(e)
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }
}