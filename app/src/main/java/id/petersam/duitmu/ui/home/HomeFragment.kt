package id.petersam.duitmu.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.FragmentHomeBinding
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.ui.create.CreateTransactionActivity
import id.petersam.duitmu.ui.filter.TransactionFilterModalFragment
import id.petersam.duitmu.ui.main.MainActivity
import id.petersam.duitmu.ui.main.MainViewModel
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.snackBar
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.toRupiah
import id.petersam.duitmu.util.viewBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val actVm by activityViewModels<MainViewModel>()

    private val adapter by lazy {
        TransactionListAdapter(object : TransactionListAdapter.Listener {
            override fun onItemClicked(id: String) {
                CreateTransactionActivity.start(requireContext(), id)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupActionView()

        observeVm()
        actVm.getTransactions()
    }

    private fun setupRecyclerView() {
        with(binding.rvTransactions) {
            adapter = this@HomeFragment.adapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupActionView() {
        binding.fabCreateTransaction.setOnClickListener {
            val intent = Intent(requireContext(), CreateTransactionActivity::class.java)
            launcher.launch(intent)
        }

        binding.sectionHomeDashboard.cardIncome.setOnClickListener {
            actVm.onTypeChanged(Transaction.Type.INCOME)
            (requireActivity() as MainActivity).navigateToChartPage()
        }
        binding.sectionHomeDashboard.cardExpense.setOnClickListener {
            actVm.onTypeChanged(Transaction.Type.EXPENSE)
            (requireActivity() as MainActivity).navigateToChartPage()
        }

        binding.etPeriod.setOnClickListener {
            showTransactionFilterModal()
        }
    }

    private fun observeVm() {
        actVm.transaction.observe(viewLifecycleOwner) { items ->
            binding.rvTransactions.isVisible = items.isNotEmpty()
            binding.groupEmptyNote.isVisible = items.isEmpty()

            adapter.submitList(items)
            showCardData(items)
        }

        actVm.datePeriod.observe(viewLifecycleOwner) {
            binding.etPeriod.setText(
                if (it == DatePeriod.CUSTOM) {
                    val startDate = actVm.startDate.value ?: return@observe
                    val endDate = actVm.endDate.value ?: return@observe
                    "${startDate.toReadableString(DatePattern.DMY_SHORT)} - " +
                            endDate.toReadableString(DatePattern.DMY_SHORT)
                } else it.readable
            )
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                requireActivity().snackBar(
                    binding.root,
                    getString(R.string.success_add_data),
                    R.color.green_text
                )
                actVm.getTransactions()
            }
        }

    private fun showTransactionFilterModal() {
        val modal = TransactionFilterModalFragment.newInstance(childFragmentManager)
        modal?.show(childFragmentManager, TransactionFilterModalFragment.TAG)
    }

    private fun showCardData(items: List<TransactionListAdapter.Item>) {
        val totalIncome = items.sumOf { it.income ?: 0L }
        val totalExpense = items.sumOf { it.expense ?: 0L }
        binding.sectionHomeDashboard.tvIncome.text = totalIncome.toRupiah()
        binding.sectionHomeDashboard.tvExpense.text = totalExpense.toRupiah()

        when {
            totalIncome > totalExpense -> {
                binding.sectionHomeDashboard.tvBalance.apply {
                    text = (totalIncome - totalExpense).toRupiah()
                    setTextColor(ContextCompat.getColor(context, R.color.green_text))
                }
                binding.sectionHomeDashboard.containerBalance.setBackgroundResource(R.drawable.green_gradient_horizontal)
            }
            totalIncome < totalExpense -> {
                binding.sectionHomeDashboard.tvBalance.apply {
                    text = "- ${(totalExpense - totalIncome).toRupiah()}"
                    setTextColor(ContextCompat.getColor(context, R.color.red_text))
                }
                binding.sectionHomeDashboard.containerBalance.setBackgroundResource(R.drawable.red_gradient_horizontal)
            }
            else -> {
                binding.sectionHomeDashboard.tvBalance.apply {
                    text = totalIncome.toRupiah()
                    setTextColor(Color.BLACK)
                }
                binding.sectionHomeDashboard.containerBalance.setBackgroundResource(android.R.color.white)
            }
        }
    }
}