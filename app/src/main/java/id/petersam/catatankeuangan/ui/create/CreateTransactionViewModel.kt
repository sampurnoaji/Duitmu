package id.petersam.catatankeuangan.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.catatankeuangan.data.TransactionRepository
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.util.DatePattern
import id.petersam.catatankeuangan.util.LoadState
import id.petersam.catatankeuangan.util.toReadableString
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

    private val _category = MutableLiveData<String?>()
    val category: LiveData<String?> get() = _category

    private val _amount = MutableLiveData<Long>()
    val amount: LiveData<Long> get() = _amount

    private val _note = MutableLiveData<String>()
    val note: LiveData<String> get() = _note

    private var trxId: String? = null
    fun setTransactionId(id: String) {
        trxId = id
    }

    fun onTypeChanged(type: Transaction.Type) {
        _type.value = type
        _category.value = null
    }

    fun onDateChanged(timeInMillis: Long) {
        val selectedDate = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }.time
        _date.value = selectedDate
    }

    fun onDateChanged(date: Date) {
        _date.value = date
    }

    fun onCategoryChanged(category: String?) {
        _category.value = category
    }

    fun onAmountChanged(amount: Long) {
        _amount.value = amount
    }

    fun onNoteChanged(note: String) {
        _note.value = note
    }

    private val _insertTransaction = MutableLiveData<LoadState<Boolean>>()
    val insertTransaction: LiveData<LoadState<Boolean>> get() = _insertTransaction

    private val _trx = MutableLiveData<LoadState<Transaction>>()
    val trx: LiveData<LoadState<Transaction>> get() = _trx

    fun getTransaction(trxId: String) {
        _trx.value = LoadState.Loading
        viewModelScope.launch {
            try {
                _trx.value = LoadState.Success(repository.getTransaction(trxId))
            } catch (e: Exception) {
                _trx.value = LoadState.Error(e.message.orEmpty())
            }
        }
    }

    fun saveTransaction() {
        viewModelScope.launch {
            _insertTransaction.value = LoadState.Loading
            try {
                val trx = Transaction(
                    id = trxId ?: Date().toReadableString(DatePattern.FULL),
                    type = _type.value ?: Transaction.Type.EXPENSE,
                    amount = _amount.value ?: 0,
                    category = _category.value.orEmpty(),
                    date = _date.value ?: Date(),
                    note = _note.value.orEmpty()
                )
                if (_trx.value == null) repository.insertTransaction(trx)
                else repository.updateTransaction(trx)

                _insertTransaction.value = LoadState.Success(true)
            } catch (e: Exception) {
                _insertTransaction.value = LoadState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun deleteTransaction() {
        _trx.value?.let {
            if (it is LoadState.Success) {
                viewModelScope.launch {
                    repository.deleteTransaction(it.data)
                }
            }
        }
    }
}