package id.petersam.dhuwite.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.dhuwite.Transaction
import id.petersam.dhuwite.databinding.ActivityMainBinding
import id.petersam.dhuwite.util.toRupiah
import id.petersam.dhuwite.util.viewBinding
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()

        binding.tvIncome.text = vm.toRecyclerViewItems().sumOf { it.income ?: 0L }.toRupiah()
        binding.tvExpense.text = vm.toRecyclerViewItems().sumOf { it.expense ?: 0L }.toRupiah()
    }

    private fun setupRecyclerView() {
        with(binding.rvTransactions) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TransactionListAdapter(vm.toRecyclerViewItems())
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                ))
        }
    }
}