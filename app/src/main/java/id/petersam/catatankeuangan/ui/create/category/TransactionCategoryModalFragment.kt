package id.petersam.catatankeuangan.ui.create.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.catatankeuangan.R
import id.petersam.catatankeuangan.databinding.FragmentTransactionCategoryModalBinding
import id.petersam.catatankeuangan.model.Category
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.ui.create.CreateTransactionActivity
import id.petersam.catatankeuangan.util.LoadState
import id.petersam.catatankeuangan.util.showDialog
import id.petersam.catatankeuangan.util.snackBar
import id.petersam.catatankeuangan.util.viewBinding

@AndroidEntryPoint
class TransactionCategoryModalFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentTransactionCategoryModalBinding::bind)
    private val vm by viewModels<TransactionCategoryViewModel>()
    private val categoriesAdapter by lazy { TransactionCategoryListAdapter(onItemListClick) }

    companion object {
        const val TAG = "TransactionCategoryModalFragment"

        fun newInstance(manager: FragmentManager): TransactionCategoryModalFragment? {
            manager.findFragmentByTag(TAG) ?: return TransactionCategoryModalFragment()
            return null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_category_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeVm()
        setupActionView()
    }

    private fun observeVm() {
        vm.categories.observe(this) {
            categoriesAdapter.submitList(it)
        }

        vm.insertCategory.observe(this) {
            when (it) {
                is LoadState.Loading -> {
                    setLoading(true)
                }
                is LoadState.Success -> {
                    setLoading(false)
                }
                is LoadState.Error -> {
                    setLoading(false)
                    requireActivity().snackBar(
                        binding.root,
                        getString(R.string.error_occured),
                        R.color.red_text
                    )
                }
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvCategories) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupActionView() {
        binding.toggleButton.addOnButtonCheckedListener { _, _, _ ->
            if (binding.btnIncome.isChecked) vm.onTypeChanged(CreateTransactionActivity.INCOME_BUTTON_INDEX)
            if (binding.btnExpense.isChecked) vm.onTypeChanged(CreateTransactionActivity.EXPENSE_BUTTON_INDEX)
        }
        vm.onTypeChanged(CreateTransactionActivity.EXPENSE_BUTTON_INDEX)

        binding.btnAdd.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.btnClose.setOnClickListener { dismiss() }
    }

    private val onItemListClick = object : TransactionCategoryListAdapter.OnItemClick {
        override fun onEditItem(category: Category) {
            showAddCategoryDialog(category)
        }

        override fun onDeleteItem(category: Category) {
            requireContext().showDialog(
                msg = "${getString(R.string.delete)} ${category.category}?",
                positiveBtn = getString(android.R.string.ok),
                positiveBtnAction = { vm.deleteCategory(category) },
                negativeBtn = getString(R.string.cancel)
            )
        }
    }

    private fun showAddCategoryDialog(category: Category? = null) {
        val view = layoutInflater.inflate(R.layout.dialog_add_category, null, false)
        val btnExpense = view?.findViewById<MaterialButton>(R.id.btnExpense)
        val btnIncome = view?.findViewById<MaterialButton>(R.id.btnIncome)
        val etCategory = view?.findViewById<EditText>(R.id.etCategory)

        requireContext().showDialog(
            view = view,
            msg = if (category == null) getString(R.string.add_category) else getString(R.string.edit_category),
            positiveBtn = getString(android.R.string.ok),
            positiveBtnAction = {
                val input = etCategory?.text.toString().trim()
                if (input.isNotEmpty()) {
                    if (btnExpense?.isChecked == true) {
                        if (category == null) vm.insertCategory(input, Transaction.Type.EXPENSE)
                        else vm.updateCategory(
                            Category(
                                id = category.id,
                                category = input,
                                type = Transaction.Type.EXPENSE
                            )
                        )
                    } else {
                        if (category == null) vm.insertCategory(input, Transaction.Type.INCOME)
                        else vm.updateCategory(
                            Category(
                                id = category.id,
                                category = input,
                                type = Transaction.Type.INCOME
                            )
                        )
                    }
                }
            },
            negativeBtn = getString(R.string.cancel)
        ).apply {
            btnExpense?.isChecked = vm.type.value == Transaction.Type.EXPENSE
            btnIncome?.isChecked = vm.type.value == Transaction.Type.INCOME
            etCategory?.setText(category?.category)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pgbLoading.isVisible = isLoading
        binding.btnAdd.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }
}