package id.petersam.duitmu.ui.create

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityCreateTransactionBinding
import id.petersam.duitmu.ui.create.category.TransactionCategoryModalFragment
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.LoadState
import id.petersam.duitmu.util.ThousandSeparatorTextWatcher
import id.petersam.duitmu.util.removeThousandSeparator
import id.petersam.duitmu.util.snackbar
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.viewBinding

@AndroidEntryPoint
class CreateTransactionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCreateTransactionBinding::inflate)
    private val vm by viewModels<CreateTransactionViewModel>()

    companion object {
        const val INCOME_BUTTON_INDEX = 0
        const val EXPENSE_BUTTON_INDEX = 1

        private const val DATE_PICKER_TAG = "date_picker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupActionView()
        observeVm()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun observeVm() {
        vm.date.observe(this) {
            binding.etDate.setText(it.toReadableString(DatePattern.DMY_LONG))
        }
        vm.categories.observe(this) {
            val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, it)
            (binding.etCategory as? AutoCompleteTextView)?.let { dropdown ->
                dropdown.setAdapter(adapter)
                dropdown.setText("")
            }
        }

        vm.insertTransaction.observe(this) {
            when (it) {
                is LoadState.Loading -> {
                    setLoading(true)
                }
                is LoadState.Success -> {
                    setLoading(false)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                is LoadState.Error -> {
                    setLoading(false)
                    snackbar(binding.root, getString(R.string.error_occured), R.color.red_text)
                }
            }
        }
    }

    private fun setupActionView() {
        binding.toggleButton.addOnButtonCheckedListener { _, _, _ ->
            if (binding.btnIncome.isChecked) vm.onTypeChanged(INCOME_BUTTON_INDEX)
            if (binding.btnExpense.isChecked) vm.onTypeChanged(EXPENSE_BUTTON_INDEX)
        }
        vm.onTypeChanged(EXPENSE_BUTTON_INDEX)

        with(binding.etDate) {
            setOnClickListener {
                if (supportFragmentManager.findFragmentByTag(DATE_PICKER_TAG) == null) {
                    MaterialDatePicker.Builder.datePicker()
                        .setSelection(vm.date.value?.time)
                        .build().apply {
                            show(supportFragmentManager, DATE_PICKER_TAG)
                            addOnPositiveButtonClickListener {
                                vm.onDateChanged(it)
                            }
                        }
                }
            }
            doOnTextChanged { _, _, _, _ -> binding.tilDate.error = null }
        }
        binding.etCategory.doOnTextChanged { text, _, _, _ ->
            binding.tilCategory.error = null
            vm.onCategoryChanged(text.toString())
        }
        binding.btnEditCategory.setOnClickListener {
            val modal = TransactionCategoryModalFragment.newInstance(supportFragmentManager)
            modal?.show(supportFragmentManager, TransactionCategoryModalFragment.TAG)
        }

        with(binding.etAmount) {
            inputType = InputType.TYPE_CLASS_NUMBER
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
            doOnTextChanged { text, _, _, _ ->
                binding.tilAmount.error = null
                vm.onAmountChanged(text.toString().removeThousandSeparator() ?: 0)
            }
        }

        binding.etNote.doOnTextChanged { text, _, _, _ ->
            vm.onNoteChanged(text.toString().trim())
        }

        binding.btnSave.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        if (vm.date.value == null) {
            binding.tilDate.error = getString(R.string.error_required_field)
            return
        }
        if (vm.category.value == null) {
            binding.tilCategory.error = getString(R.string.error_required_field)
            return
        }
        if (vm.amount.value == null) {
            binding.tilAmount.error = getString(R.string.error_required_field)
            return
        }
        vm.saveTransaction()
    }

    private fun setLoading(isVisible: Boolean) {
        binding.pgbLoading.isVisible = isVisible
        binding.btnSave.isEnabled = !isVisible
    }
}