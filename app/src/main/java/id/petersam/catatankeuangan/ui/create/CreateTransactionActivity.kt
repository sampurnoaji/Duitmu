package id.petersam.catatankeuangan.ui.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.catatankeuangan.R
import id.petersam.catatankeuangan.databinding.ActivityCreateTransactionBinding
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.ui.create.category.TransactionCategoryModalFragment
import id.petersam.catatankeuangan.util.DatePattern
import id.petersam.catatankeuangan.util.LoadState
import id.petersam.catatankeuangan.util.ThousandSeparatorTextWatcher
import id.petersam.catatankeuangan.util.addThousandSeparator
import id.petersam.catatankeuangan.util.alertDialog
import id.petersam.catatankeuangan.util.removeThousandSeparator
import id.petersam.catatankeuangan.util.snackBar
import id.petersam.catatankeuangan.util.toReadableString
import id.petersam.catatankeuangan.util.viewBinding

@AndroidEntryPoint
class CreateTransactionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCreateTransactionBinding::inflate)
    private val vm by viewModels<CreateTransactionViewModel>()

    companion object {
        const val INCOME_BUTTON_INDEX = 0
        const val EXPENSE_BUTTON_INDEX = 1

        private const val DATE_PICKER_TAG = "date_picker"

        private const val INTENT_KEY_TRX_ID = "trxId"

        @JvmStatic
        fun start(context: Context, trxId: String) {
            val starter = Intent(context, CreateTransactionActivity::class.java)
                .putExtra(INTENT_KEY_TRX_ID, trxId)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupActionView()
        observeInputParams()
        observeTransactionDetail()
        observeInsertTransaction()

        intent.extras?.let {
            val trxId = it.getString(INTENT_KEY_TRX_ID) ?: return
            supportActionBar?.title = getString(R.string.edit_category)
            vm.getTransaction(trxId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun observeInputParams() {
        vm.type.observe(this) {
            binding.toggleButton.check(
                if (it == Transaction.Type.INCOME) binding.btnIncome.id
                else binding.btnExpense.id
            )
        }
        vm.date.observe(this) {
            binding.etDate.setText(it.toReadableString(DatePattern.DMY_LONG))
        }
        vm.categories.observe(this) {
            val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, it)
            (binding.etCategory as? AutoCompleteTextView)?.setAdapter(adapter)

            if (binding.etDate.isEnabled) vm.onCategoryChanged(null)
        }
        vm.category.observe(this) {
            (binding.etCategory as? AutoCompleteTextView)?.apply {
                if (text.toString() != it) setText(it)
            }
        }
        vm.amount.observe(this) {
            binding.etAmount.apply {
                if (text.toString().removeThousandSeparator() != it)
                    setText(it.toString().addThousandSeparator())
            }
        }
        vm.note.observe(this) {
            binding.etNote.apply {
                if (text.toString() != it) setText(it)
            }
        }
    }

    private fun observeTransactionDetail() {
        vm.trx.observe(this) {
            when (it) {
                is LoadState.Loading -> {
                    setLoading(true)
                }
                is LoadState.Success -> {
                    setLoading(false)
                    with(it.data) {
                        vm.setTransactionId(id)
                        vm.onTypeChanged(type)
                        vm.onDateChanged(date)
                        vm.onCategoryChanged(category)
                        vm.onAmountChanged(amount)
                        vm.onNoteChanged(note)
                    }
                    disableForm()
                }
                is LoadState.Error -> {
                    setLoading(false)
                    snackBar(binding.appBarLayout,
                        getString(R.string.error_occured),
                        R.color.red_text,
                        duration = Snackbar.LENGTH_INDEFINITE,
                        actionText = getString(R.string.close),
                        action = { finish() }
                    )
                }
            }
        }
    }

    private fun observeInsertTransaction() {
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
                    snackBar(binding.root, getString(R.string.error_occured), R.color.red_text)
                }
            }
        }
    }

    private fun setupActionView() {
        binding.toggleButton.addOnButtonCheckedListener { _, _, _ ->
            if (binding.btnIncome.isChecked) vm.onTypeChanged(Transaction.Type.INCOME)
            if (binding.btnExpense.isChecked) vm.onTypeChanged(Transaction.Type.EXPENSE)
        }
        vm.onTypeChanged(Transaction.Type.EXPENSE)

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
            vm.onNoteChanged(text.toString())
        }

        binding.btnSave.setOnClickListener {
            validateInput()
        }

        binding.btnDelete.setOnClickListener {
            alertDialog(
                message = getString(R.string.dialog_msg_delete_confirmation),
                positiveButtonText = getString(R.string.delete),
                positiveAction = {
                    vm.deleteTransaction()
                    finish()
                },
                negativeButtonText = getString(R.string.cancel)
            )
        }

        binding.btnEdit.setOnClickListener {
            enableForm()
        }
    }

    private fun validateInput() {
        if (vm.date.value == null) {
            binding.tilDate.error = getString(R.string.error_required_field)
            return
        }
        if (vm.category.value.isNullOrEmpty()) {
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

    private fun enableForm() {
        with(binding) {
            btnIncome.isEnabled = true
            btnExpense.isEnabled = true

            tilDate.apply {
                boxStrokeWidth = 1
                boxBackgroundColor = getColor(R.color.white)
            }
            etDate.isEnabled = true
            tilCategory.apply {
                boxStrokeWidth = 1
                boxBackgroundColor = getColor(R.color.white)
                endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
            }
            etCategory.isEnabled = true
            tilAmount.apply {
                boxStrokeWidth = 1
                boxBackgroundColor = getColor(R.color.white)
            }
            etAmount.isEnabled = true
            tilNote.apply {
                boxStrokeWidth = 1
                boxBackgroundColor = getColor(R.color.white)
            }
            etNote.isEnabled = true

            btnEditCategory.isVisible = true
            btnSave.isVisible = true
            btnDelete.isVisible = false
            btnEdit.isVisible = false
        }
    }

    private fun disableForm() {
        with(binding) {
            btnIncome.isEnabled = vm.type.value == Transaction.Type.INCOME
            btnExpense.isEnabled = vm.type.value == Transaction.Type.EXPENSE

            tilDate.apply {
                boxStrokeWidth = 0
                boxBackgroundColor = getColor(R.color.edittext_disable)
            }
            etDate.isEnabled = false
            tilCategory.apply {
                boxStrokeWidth = 0
                boxBackgroundColor = getColor(R.color.edittext_disable)
                endIconMode = TextInputLayout.END_ICON_NONE
            }
            etCategory.isEnabled = false
            btnEditCategory.isVisible = false
            tilAmount.apply {
                boxStrokeWidth = 0
                boxBackgroundColor = getColor(R.color.edittext_disable)
            }
            etAmount.isEnabled = false
            tilNote.apply {
                boxStrokeWidth = 0
                boxBackgroundColor = getColor(R.color.edittext_disable)
            }
            etNote.isEnabled = false

            btnEditCategory.isVisible = false
            btnSave.isVisible = false
            btnDelete.isVisible = true
            btnEdit.isVisible = true
        }
    }
}