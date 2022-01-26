package id.petersam.duitmu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.duitmu.data.TransactionRepository
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.toDate
import id.petersam.duitmu.util.toReadableString
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(transactionRepository: TransactionRepository) :
    ViewModel() {

    val transaction: LiveData<List<TransactionListAdapter.Item>> =
        transactionRepository.getTransactions().asLiveData().map {
            it.toRecyclerViewItems()
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

    private val _startDate = MutableLiveData<Date>()
    val startDate: LiveData<Date> get() = _startDate

    private val _endDate = MutableLiveData<Date>()
    val endDate: LiveData<Date> get() = _endDate

    fun onDatePeriodChanged(filter: String) {
        DatePeriod.values().forEach {
            if (filter == it.readable) _datePeriod.value = it
        }
    }

    fun onDatePeriodChanged(startDate: Date, endDate: Date) {
        val datePeriod = _datePeriod.value
    }

    fun onStartDateChanged(date: Date) {
        _startDate.value = date
    }

    fun onEndDateChanged(date: Date) {
        _endDate.value = date
    }
}