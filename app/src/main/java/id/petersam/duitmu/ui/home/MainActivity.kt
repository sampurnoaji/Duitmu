package id.petersam.duitmu.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityMainBinding
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.ui.chart.TransactionChartActivity
import id.petersam.duitmu.ui.create.CreateTransactionActivity
import id.petersam.duitmu.ui.filter.TransactionFilterModalFragment
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.snackBar
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.toRupiah
import id.petersam.duitmu.util.viewBinding
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    private val adapter by lazy { TransactionListAdapter(itemListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        setupActionView()

        observeVm()
    }

    private fun observeVm() {
        vm.transaction.observe(this) { items ->
            binding.rvTransactions.isVisible = items.isNotEmpty()
            binding.groupEmptyNote.isVisible = items.isEmpty()

            adapter.submitList(items)
            binding.tvIncome.text = items.sumOf { it.income ?: 0L }.toRupiah()
            binding.tvExpense.text = items.sumOf { it.expense ?: 0L }.toRupiah()
        }

        vm.datePeriod.observe(this) {
            binding.etPeriod.setText(
                if (it == DatePeriod.CUSTOM) {
                    "${vm.startDate.value?.toReadableString(DatePattern.DMY_SHORT)} - " +
                            "${vm.endDate.value?.toReadableString(DatePattern.DMY_SHORT)}"
                } else it.readable
            )
        }
    }

    private fun setupActionView() {
        binding.fabCreateTransaction.setOnClickListener {
            val intent = Intent(this, CreateTransactionActivity::class.java)
            launcher.launch(intent)
        }

        binding.cardIncome.setOnClickListener {
            TransactionChartActivity.start(this, TransactionChartActivity.INCOME_BUTTON_INDEX)
        }
        binding.cardExpense.setOnClickListener {
            TransactionChartActivity.start(this, TransactionChartActivity.EXPENSE_BUTTON_INDEX)
        }

        binding.etPeriod.setOnClickListener {
            val modal = TransactionFilterModalFragment.newInstance(supportFragmentManager)
            modal?.show(supportFragmentManager, TransactionFilterModalFragment.TAG)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                snackBar(
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

    private val itemListener = object : TransactionListAdapter.Listener {
        override fun onItemClicked(id: String) {
            CreateTransactionActivity.start(this@MainActivity, id)
        }
    }
}