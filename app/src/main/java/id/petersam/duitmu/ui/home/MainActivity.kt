package id.petersam.duitmu.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityMainBinding
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.ui.chart.TransactionChartFragment
import id.petersam.duitmu.ui.create.CreateTransactionActivity
import id.petersam.duitmu.ui.filter.TransactionFilterModalFragment
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.snackBar
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.toRupiah
import id.petersam.duitmu.util.viewBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    private val adapter by lazy { TransactionListAdapter(itemListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupActionView()

        observeVm()
        vm.getTransactions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuPeriod -> {
                showTransactionFilterModal()
                true
            }
            R.id.menuChart -> {
                showChartFragment(Transaction.Type.INCOME)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeVm() {
        vm.transaction.observe(this) { items ->
            binding.rvTransactions.isVisible = items.isNotEmpty()
            binding.groupEmptyNote.isVisible = items.isEmpty()

            adapter.submitList(items)
            showCardData(items)
        }

        vm.datePeriod.observe(this) {
            binding.etPeriod.setText(
                if (it == DatePeriod.CUSTOM) {
                    val startDate = vm.startDate.value ?: return@observe
                    val endDate = vm.endDate.value ?: return@observe
                    "${startDate.toReadableString(DatePattern.DMY_SHORT)} - " +
                            endDate.toReadableString(DatePattern.DMY_SHORT)
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
            showChartFragment(Transaction.Type.INCOME)
        }
        binding.cardExpense.setOnClickListener {
            showChartFragment(Transaction.Type.EXPENSE)
        }

        binding.etPeriod.setOnClickListener {
            showTransactionFilterModal()
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
                vm.getTransactions()
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

    private fun showCardData(items: List<TransactionListAdapter.Item>) {
        val totalIncome = items.sumOf { it.income ?: 0L }
        val totalExpense = items.sumOf { it.expense ?: 0L }
        binding.tvIncome.text = totalIncome.toRupiah()
        binding.tvExpense.text = totalExpense.toRupiah()

        when {
            totalIncome > totalExpense -> {
                binding.tvBalance.apply {
                    text = (totalIncome - totalExpense).toRupiah()
                    setTextColor(ContextCompat.getColor(context, R.color.green_text))
                }
                binding.containerBalance.setBackgroundResource(R.drawable.green_gradient_horizontal)
            }
            totalIncome < totalExpense -> {
                binding.tvBalance.apply {
                    text = "- ${(totalExpense - totalIncome).toRupiah()}"
                    setTextColor(ContextCompat.getColor(context, R.color.red_text))
                }
                binding.containerBalance.setBackgroundResource(R.drawable.red_gradient_horizontal)
            }
            else -> {
                binding.tvBalance.apply {
                    text = totalIncome.toRupiah()
                    setTextColor(Color.BLACK)
                }
                binding.containerBalance.setBackgroundResource(android.R.color.white)
            }
        }
    }

    private fun showChartFragment(type: Transaction.Type) {
        vm.onTypeChanged(type)
        val fragment = TransactionChartFragment.newInstance(supportFragmentManager)
        fragment?.show(supportFragmentManager, TransactionChartFragment.TAG)
    }

    private fun showTransactionFilterModal() {
        val modal = TransactionFilterModalFragment.newInstance(supportFragmentManager)
        modal?.show(supportFragmentManager, TransactionFilterModalFragment.TAG)
    }
}