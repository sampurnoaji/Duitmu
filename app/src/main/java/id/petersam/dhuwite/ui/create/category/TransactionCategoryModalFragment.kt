package id.petersam.dhuwite.ui.create.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.dhuwite.R
import id.petersam.dhuwite.databinding.FragmentTransactionCategoryModalBinding
import id.petersam.dhuwite.model.Transaction
import id.petersam.dhuwite.ui.create.CreateTransactionActivity
import id.petersam.dhuwite.util.showDialog
import id.petersam.dhuwite.util.viewBinding

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
    }

    private val onItemListClick = object : TransactionCategoryListAdapter.OnItemClick {
        override fun onEditItem(category: String) {
            showAddCategoryDialog(category)
        }

        override fun onDeleteItem(category: String) {
            requireContext().showDialog(
                msg = "${getString(R.string.delete)} $category?",
                positiveBtn = getString(android.R.string.ok),
                positiveBtnAction = {

                },
                negativeBtn = getString(R.string.cancel)
            )
        }
    }

    private fun showAddCategoryDialog(category: String? = null) {
        val view = layoutInflater.inflate(R.layout.dialog_add_category, null, false)
        val btnExpense = view?.findViewById<MaterialButton>(R.id.btnExpense)
        val btnIncome = view?.findViewById<MaterialButton>(R.id.btnIncome)
        val etCategory = view?.findViewById<EditText>(R.id.etCategory)

        requireContext().showDialog(
            view = view,
            msg = if (category.isNullOrEmpty()) getString(R.string.add_category) else getString(R.string.edit_category),
            positiveBtn = getString(android.R.string.ok),
            positiveBtnAction = {
                val input = etCategory?.text.toString().trim()
                if (input.isNotEmpty()) {
                    if (btnExpense?.isChecked == true) {
                        if (category.isNullOrEmpty()) vm.addTransactionExpenseCategory(input)
                        else vm.updateTransactionExpenseCategory(category, input)
                    } else {
                        if (category.isNullOrEmpty()) vm.addTransactionIncomeCategory(input)
                        else vm.updateTransactionIncomeCategory(category, input)
                    }
                }
            },
            negativeBtn = getString(R.string.cancel)
        ).apply {
            btnExpense?.isChecked = vm.type.value == Transaction.Type.EXPENSE
            btnIncome?.isChecked = vm.type.value == Transaction.Type.INCOME
            etCategory?.setText(category)
        }
    }
}