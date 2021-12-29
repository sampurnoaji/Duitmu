package id.petersam.dhuwite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.petersam.dhuwite.databinding.ActivityMainBinding
import id.petersam.dhuwite.util.viewBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding.rvTransactions) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TransactionListAdapter(items)
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                ))
        }
    }

    private val items = with(TransactionListAdapter) {
        listOf(
            TransactionListAdapter.Item(VIEW_TYPE_HEADER),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(VIEW_TYPE_HEADER),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(VIEW_TYPE_HEADER),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.INCOME, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(VIEW_TYPE_HEADER),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            ),
            TransactionListAdapter.Item(
                VIEW_TYPE_CHILD, Transaction(
                    1, Transaction.Type.EXPENSE, Date(), "Makanan", "Seblak", 75000L
                )
            )
        )
    }
}