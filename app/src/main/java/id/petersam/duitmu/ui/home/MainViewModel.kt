package id.petersam.duitmu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.duitmu.data.TransactionRepository
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.toDate
import id.petersam.duitmu.util.toReadableString
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: TransactionRepository) :
    ViewModel() {

    private val _transaction = MutableLiveData<List<TransactionListAdapter.Item>>()
    val transaction: LiveData<List<TransactionListAdapter.Item>> get() = _transaction

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
                    }
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

    private val _datePeriod = MutableLiveData(DatePeriod.ALL)
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
                _transaction.value = it.toRecyclerViewItems()
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

    /*region Chart*/
    var incomeCategories: List<Pair<String, Long>>
    var expensesCategories: List<Pair<String, Long>>

    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type>
        get() = _type

    private val _lineChartData = MutableLiveData<List<Pair<String, Long>>>()
    val lineChartData: LiveData<List<Pair<String, Long>>>
        get() = _lineChartData

    init {
        incomeCategories = getIncomeCategoryPercentage()
        expensesCategories = getExpenseCategoryPercentage()
    }

    fun onTypeChanged(type: Transaction.Type) {
        _type.value = type
        if (type == Transaction.Type.INCOME) getLineChartIncomeData()
        else getLineChartExpenseData()
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

    fun prependEmpty(data: List<Pair<String, Long>>): List<Pair<String, Long>> {
        return data.toMutableList().apply {
            add(0, Pair("", 0))
        }
    }

    private fun getIncomeCategoryPercentage(): List<Pair<String, Long>> = runBlocking {
        val categoriesDeferred = async { repository.getIncomeCategoryPercentage() }
        categoriesDeferred.await().sortedByDescending { it.second }
    }

    private fun getExpenseCategoryPercentage(): List<Pair<String, Long>> = runBlocking {
        val categoriesDeferred = async { repository.getExpenseCategoryPercentage() }
        categoriesDeferred.await().sortedByDescending { it.second }
    }

    fun getIncomesCategoryPercentageLabels(): List<Float> {
        val amountTotal = incomeCategories.sumOf { it.second }.toFloat()
        return incomeCategories.map {
            (it.second / amountTotal) * 100
        }
    }

    fun getExpensesCategoryPercentageLabels(): List<Float> {
        val amountTotal = expensesCategories.sumOf { it.second }.toFloat()
        return expensesCategories.map {
            (it.second / amountTotal) * 100
        }
    }
    /*endregion*/
}