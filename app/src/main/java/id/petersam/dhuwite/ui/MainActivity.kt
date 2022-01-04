package id.petersam.dhuwite.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.dhuwite.R
import id.petersam.dhuwite.databinding.ActivityMainBinding
import id.petersam.dhuwite.ui.create.CreateTransactionActivity
import id.petersam.dhuwite.util.snackbar
import id.petersam.dhuwite.util.toRupiah
import id.petersam.dhuwite.util.viewBinding
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    private val adapter by lazy { TransactionListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()

        vm.transaction.observe(this) { items ->
            binding.rvTransactions.isVisible = items.isNotEmpty()
            binding.groupEmptyNote.isVisible = items.isEmpty()

            adapter.submitList(items)
            binding.tvIncome.text = items.sumOf { it.income ?: 0L }.toRupiah()
            binding.tvExpense.text = items.sumOf { it.expense ?: 0L }.toRupiah()
        }

        binding.fabCreateTransaction.setOnClickListener {
            val intent = Intent(this, CreateTransactionActivity::class.java)
            launcher.launch(intent)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                snackbar(
                    binding.root,
                    getString(R.string.success_add_data),
                    R.color.green_text
                )
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
                )
            )
        }
    }
}