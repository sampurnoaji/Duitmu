package id.petersam.duitmu.ui.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.duitmu.data.TransactionRepository
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.LoadState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class TransactionChartViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    var incomeData: List<Pair<String, Long>>
    var expenseData: List<Pair<String, Long>>

    private val _type = MutableLiveData<Transaction.Type>()
    val type: LiveData<Transaction.Type> get() = _type

    init {
        incomeData = getIncomes()
        expenseData = getExpenses()
    }

    fun onTypeChanged(index: Int) {
        _type.value =
            if (index == TransactionChartActivity.INCOME_BUTTON_INDEX) Transaction.Type.INCOME
            else Transaction.Type.EXPENSE
    }

    private fun getIncomes(): List<Pair<String, Long>> = runBlocking {
        val incomesDeferred = async { repository.getSummaryIncomeTransactions() }
        incomesDeferred.await()
    }

    private fun getExpenses(): List<Pair<String, Long>> = runBlocking {
        val expensesDeferred = async { repository.getSummaryExpenseTransactions() }
        expensesDeferred.await()
    }
}