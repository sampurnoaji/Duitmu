package id.petersam.duitmu.ui.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.duitmu.data.TransactionRepository
import id.petersam.duitmu.model.Transaction
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.math.exp

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
        var incomes = incomesDeferred.await()
        if (incomes.isNotEmpty()) incomes = incomes.prependEmpty()
        incomes
    }

    private fun getExpenses(): List<Pair<String, Long>> = runBlocking {
        val expensesDeferred = async { repository.getSummaryExpenseTransactions() }
        var expenses = expensesDeferred.await()
        if (expenses.isNotEmpty()) expenses = expenses.prependEmpty()
        expenses
    }

    private fun List<Pair<String, Long>>.prependEmpty(): List<Pair<String, Long>> {
        return toMutableList().apply {
            add(0, Pair("", 0))
        }
    }
}