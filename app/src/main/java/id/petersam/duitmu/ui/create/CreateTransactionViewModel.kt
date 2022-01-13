package id.petersam.duitmu.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.duitmu.data.TransactionRepository
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.LoadState
import id.petersam.duitmu.util.toReadableString
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type> get() = _type

    private val _date = MutableLiveData(Date())
    val date: LiveData<Date> get() = _date

    private val _expenseCategories = repository.getExpenseCategories().asLiveData()
    private val _incomeCategories = repository.getIncomeCategories().asLiveData()
    val categories = MediatorLiveData<List<String>>().apply {
        addSource(_type) { type ->
            value =
                if (type == Transaction.Type.EXPENSE) _expenseCategories.value?.map { it.category }
                else _incomeCategories.value?.map { it.category }
        }
        addSource(_expenseCategories) { list ->
            if (_type.value == Transaction.Type.EXPENSE) value = list.map { it.category }
        }
        addSource(_incomeCategories) { list ->
            if (_type.value == Transaction.Type.INCOME) value = list.map { it.category }
        }
    }

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    private val _amount = MutableLiveData<Long>()
    val amount: LiveData<Long> get() = _amount

    private var _note: String? = null

    private val _insertTransaction = MutableLiveData<LoadState<Boolean>>()
    val insertTransaction: LiveData<LoadState<Boolean>> get() = _insertTransaction

    fun onTypeChanged(index: Int) {
        _type.value =
            if (index == CreateTransactionActivity.INCOME_BUTTON_INDEX) Transaction.Type.INCOME
            else Transaction.Type.EXPENSE
        _category.value = null
    }

    fun onDateChanged(timeInMillis: Long) {
        val selectedDate = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }.time
        _date.value = selectedDate
    }

    fun onCategoryChanged(category: String) {
        _category.value = category
    }

    fun onAmountChanged(amount: Long) {
        _amount.value = amount
    }

    fun onNoteChanged(note: String) {
        _note = note
    }

    fun saveTransaction() {
        viewModelScope.launch {
            _insertTransaction.value = LoadState.Loading
            try {
                repository.insertTransaction(
                    Transaction(
                        id = Date().toReadableString(DatePattern.FULL),
                        type = _type.value ?: Transaction.Type.EXPENSE,
                        amount = _amount.value ?: 0,
                        category = _category.value.orEmpty(),
                        date = _date.value ?: Date(),
                        note = _note.orEmpty()
                    )
                )
                _insertTransaction.value = LoadState.Success(true)
            } catch (e: Exception) {
                _insertTransaction.value = LoadState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}