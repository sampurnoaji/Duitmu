package id.petersam.dhuwite.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.dhuwite.databinding.ActivityMainBinding
import id.petersam.dhuwite.util.toRupiah
import id.petersam.dhuwite.util.viewBinding
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    private val adapter by lazy { TransactionListAdapter(emptyList()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()

        vm.transaction.observe(this) { items ->
            adapter.submitList(items)
            binding.tvIncome.text = items.sumOf { it.income ?: 0L }.toRupiah()
            binding.tvExpense.text = items.sumOf { it.expense ?: 0L }.toRupiah()
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvTransactions) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                ))
        }
    }
}