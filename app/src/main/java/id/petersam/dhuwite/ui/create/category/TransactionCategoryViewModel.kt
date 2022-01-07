package id.petersam.dhuwite.ui.create.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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

    private val _categories = repository.getCategories().asLiveData()
    val categories = MediatorLiveData<List<String>>().apply {
        addSource(_type) {
            value = _categories.value?.filter { category ->
                category.type == it
            }?.map { it.category }
        }
        addSource(_categories) {
            value = if (_type.value == Transaction.Type.EXPENSE) it.map { category ->
                category.category
            } else {
                it.map { category ->
                    category.category
                }
            }
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

    fun addTransactionIncomeCategory(category: String) {
        repository.addTransactionIncomeCategory(category)
    }

    fun updateTransactionExpenseCategory(oldCategory: String, newCategory: String) {
        repository.updateTransactionExpenseCategory(oldCategory, newCategory)
    }

    fun updateTransactionIncomeCategory(oldCategory: String, newCategory: String) {
        repository.updateTransactionIncomeCategory(oldCategory, newCategory)
    }

    fun deleteTransactionCategory(category: String) {
        if (_type.value == Transaction.Type.EXPENSE)
            repository.deleteTransactionExpenseCategory(category)
        else
            repository.deleteTransactionIncomeCategory(category)
    }
}