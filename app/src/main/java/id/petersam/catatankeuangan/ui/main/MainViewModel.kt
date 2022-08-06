package id.petersam.catatankeuangan.ui.main

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.catatankeuangan.data.TransactionRepository
import id.petersam.catatankeuangan.data.google.GoogleDrive
import id.petersam.catatankeuangan.model.DatePeriod
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.ui.home.TransactionListAdapter
import id.petersam.catatankeuangan.util.DatePattern
import id.petersam.catatankeuangan.util.LoadState
import id.petersam.catatankeuangan.util.toDate
import id.petersam.catatankeuangan.util.toReadableString
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val googleDrive: GoogleDrive
) : ViewModel() {

    private val _querySearch = MutableLiveData("")
    private val _transactions = MutableLiveData<List<TransactionListAdapter.Item>>()
    val shownTransactions = MediatorLiveData<List<TransactionListAdapter.Item>>().apply {
        addSource(_transactions) {
            value = if (_querySearch.value.isNullOrEmpty()) {
                _transactions.value
            } else {
                _transactions.value?.filter {
                    it.transaction!!.category.lowercase().contains(
                        _querySearch.value!!.lowercase()
                    )
                }
            }
        }
        addSource(_querySearch) { query ->
            value = if (query.isNullOrEmpty()) {
                _transactions.value
            } else {
                _transactions.value?.filter {
                    it.transaction!!.category.lowercase().contains(query.lowercase())
                }
            }
        }
    }

    private val _datePeriod = MutableLiveData(DatePeriod.CURRENT_MONTH)
    val datePeriod: LiveData<DatePeriod> get() = _datePeriod

    private val _startDate = MutableLiveData<Date?>()
    val startDate: LiveData<Date?> get() = _startDate

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> get() = _endDate

    fun getTransactions() {
        val result = when (_datePeriod.value) {
            DatePeriod.CUSTOM -> repository.getTransactions(_startDate.value, _endDate.value)
            else -> repository.getTransactions(
                _datePeriod.value?.startDate,
                _datePeriod.value?.endDate
            )
        }
        viewModelScope.launch {
            result.collect {
                _transactions.value = it.toRecyclerViewItems()
            }
        }
    }

    fun onDatePeriodChanged(filter: String) {
        DatePeriod.values().forEach {
            if (filter == it.readable) _datePeriod.value = it
        }
    }

    fun onStartDateChanged(date: Date?) {
        _startDate.value = date
    }

    fun onEndDateChanged(date: Date?) {
        _endDate.value = date
    }

    fun onQuerySearchChanged(query: String) {
        _querySearch.value = query
    }

    private fun List<Transaction>.toRecyclerViewItems(): List<TransactionListAdapter.Item> {
        val items = mutableListOf<TransactionListAdapter.Item>()
        val group = this.groupBy { it.date.toReadableString(DatePattern.DMY_LONG) }
        group.entries.forEach {
            items.add(
                TransactionListAdapter.Item(
                    TransactionListAdapter.VIEW_TYPE_HEADER,
                    date = it.key.toDate(DatePattern.DMY_LONG),
                    income = it.value.sumOf { trx ->
                        if (trx.type == Transaction.Type.INCOME) trx.amount else 0
                    },
                    expense = it.value.sumOf { trx ->
                        if (trx.type == Transaction.Type.EXPENSE) trx.amount else 0
                    },
                    transaction = it.value[0]
                )
            )
            it.value.forEach { trx ->
                items.add(
                    TransactionListAdapter.Item(
                        TransactionListAdapter.VIEW_TYPE_CHILD,
                        transaction = trx
                    )
                )
            }
        }
        return items
    }

    /*region Chart*/
    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type>
        get() = _type

    private val _lineChartData = MutableLiveData<List<Pair<String, Long>>>()
    val lineChartData: LiveData<List<Pair<String, Long>>>
        get() = _lineChartData

    private val _pieChartData = MutableLiveData<List<Pair<String, Long>>>()
    val pieChartData: LiveData<List<Pair<String, Long>>>
        get() = _pieChartData

    fun onTypeChanged(type: Transaction.Type) {
        _type.value = type
        if (type == Transaction.Type.INCOME) {
            getLineChartIncomeData()
            getPieChartIncomeData()
        } else {
            getLineChartExpenseData()
            getPieChartExpenseData()
        }
    }

    fun updateChartData() {
        _type.value?.let { onTypeChanged(it) }
    }

    private fun getLineChartIncomeData() {
        viewModelScope.launch {
            _lineChartData.value = if (_datePeriod.value == DatePeriod.CUSTOM)
                repository.getSummaryIncomeTransactions(
                    _startDate.value,
                    _endDate.value
                )
            else repository.getSummaryIncomeTransactions(
                _datePeriod.value?.startDate,
                _datePeriod.value?.endDate
            )
        }
    }

    private fun getLineChartExpenseData() {
        viewModelScope.launch {
            _lineChartData.value =
                if (_datePeriod.value == DatePeriod.CUSTOM) repository.getSummaryExpenseTransactions(
                    _startDate.value,
                    _endDate.value
                )
                else repository.getSummaryExpenseTransactions(
                    _datePeriod.value?.startDate,
                    _datePeriod.value?.endDate
                )
        }
    }

    private fun getPieChartIncomeData() {
        viewModelScope.launch {
            _pieChartData.value = if (_datePeriod.value == DatePeriod.CUSTOM)
                repository.getIncomeCategoryPercentage(
                    _startDate.value,
                    _endDate.value
                )
            else repository.getIncomeCategoryPercentage(
                _datePeriod.value?.startDate,
                _datePeriod.value?.endDate
            )
        }
    }

    private fun getPieChartExpenseData() {
        viewModelScope.launch {
            _pieChartData.value = if (_datePeriod.value == DatePeriod.CUSTOM)
                repository.getExpenseCategoryPercentage(
                    _startDate.value,
                    _endDate.value
                )
            else repository.getExpenseCategoryPercentage(
                _datePeriod.value?.startDate,
                _datePeriod.value?.endDate
            )
        }
    }

    fun prependEmpty(data: List<Pair<String, Long>>): List<Pair<String, Long>> {
        return data.toMutableList().apply {
            add(0, Pair("", 0))
        }
    }

    fun getCategoryPercentageLabels(): List<Float>? {
        val amountTotal = _pieChartData.value?.sumOf { it.second }?.toFloat() ?: return null
        return _pieChartData.value?.map {
            (it.second / amountTotal) * 100
        }
    }
    /*endregion*/

    /*region Backup*/
    fun canBackupContent(): Boolean {
        val latestBackup = googleDrive.getLatestBackupTime()?.toDate(DatePattern.DRIVE)?.time
            ?: return true
        val oneDayInMillis = 86400000
        val now = Date().time
        return (now - latestBackup) > oneDayInMillis
    }

    private val _backup = MutableLiveData<LoadState<Boolean>>()
    val backup: LiveData<LoadState<Boolean>>
        get() = _backup

    private val _sync = MutableLiveData<LoadState<Boolean>>()
    val sync: LiveData<LoadState<Boolean>>
        get() = _sync

    private val _loginIntent = MutableLiveData<Pair<GoogleAction, Intent>>()
    val loginIntent: LiveData<Pair<GoogleAction, Intent>>
        get() = _loginIntent

    fun backup() {
        viewModelScope.launch {
            try {
                val status = googleDrive.checkStatus()
                if (status == null) {
                    _loginIntent.value = Pair(GoogleAction.BACKUP, googleDrive.login())
                    return@launch
                }

                _backup.value = LoadState.Loading
                googleDrive.uploadFile()
                _backup.value = LoadState.Success(true)
            } catch (e: Exception) {
                _backup.value = LoadState.Error(msg = e.message)
                e.printStackTrace()
            }
        }
    }

    fun sync() {
        viewModelScope.launch {
            try {
                val status = googleDrive.checkStatus()
                if (status == null) {
                    _loginIntent.value = Pair(GoogleAction.SYNC, googleDrive.login())
                    return@launch
                }

                _sync.value = LoadState.Loading
                googleDrive.downloadFile()
                _sync.value = LoadState.Success(true)
            } catch (e: Exception) {
                _sync.value = LoadState.Error(msg = e.message)
                e.printStackTrace()
            }
        }
    }

    enum class GoogleAction {
        BACKUP,
        SYNC,
    }
    /*endregion*/
}