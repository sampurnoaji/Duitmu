package id.petersam.dhuwite.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.dhuwite.data.TransactionRepository
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.LoadState
import id.petersam.dhuwite.util.toReadableString
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private var _type = Transaction.Type.EXPENSE

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date> get() = _date

    val incomeCategories = repository.getIncomeCategories()
    private val _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    private val _amount = MutableLiveData<Long>()
    val amount: LiveData<Long> get() = _amount

    private var _note: String? = null

    private val _insertTransaction = MutableLiveData<LoadState<Boolean>>()
    val insertTransaction: LiveData<LoadState<Boolean>> get() = _insertTransaction

    fun onTypeChanged(index: Int) {
        _type =
            if (index == CreateTransactionActivity.INCOME_BUTTON_INDEX) Transaction.Type.INCOME
            else Transaction.Type.EXPENSE
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
                        type = _type,
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