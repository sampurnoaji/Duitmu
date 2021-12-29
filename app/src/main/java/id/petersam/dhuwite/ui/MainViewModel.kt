package id.petersam.dhuwite.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.dhuwite.Transaction
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.toDate
import id.petersam.dhuwite.util.toReadableString
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val raws = listOf(
        Transaction(
            1,
            Transaction.Type.INCOME,
            Date().addDays(1),
            "Rejeki Nomplok",
            "Undi hadiah",
            1_000_000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
        ),
        Transaction(
            1, Transaction.Type.INCOME, Date(), "Makanan", "Jual Seblak", 75000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date().addDays(1), "Makanan", "Seblak", 75_000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date().addDays(1), "Makanan", "Lotek", 25_000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date(), "Makanan", "Jual Seblak", 75000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date().addDays(2), "Makanan", "Seblak", 75000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date().addDays(2), "Makanan", "Seblak", 75000L
        ),
        Transaction(
            1, Transaction.Type.EXPENSE, Date().addDays(2), "Makanan", "Seblak", 75000L
        ),
    )

    fun toRecyclerViewItems(): List<TransactionListAdapter.Item> {
        val items = mutableListOf<TransactionListAdapter.Item>()
        val group = raws.sortedByDescending { it.date }
            .groupBy { it.date.toReadableString(DatePattern.DMY_LONG) }
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

    private fun Date.addDays(daysAddition: Int): Date {
        return Calendar.getInstance().apply {
            time = this@addDays
            add(Calendar.DATE, daysAddition)
        }.time
    }
}