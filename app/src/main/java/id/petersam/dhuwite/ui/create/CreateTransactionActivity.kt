package id.petersam.dhuwite.ui.create

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.dhuwite.R
import id.petersam.dhuwite.databinding.ActivityCreateTransactionBinding
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.ThousandSeparatorTextWatcher
import id.petersam.dhuwite.util.removeThousandSeparator
import id.petersam.dhuwite.util.toReadableString
import id.petersam.dhuwite.util.viewBinding

@AndroidEntryPoint
class CreateTransactionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCreateTransactionBinding::inflate)
    private val vm by viewModels<CreateTransactionViewModel>()

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
    }

    private fun setupActionView() {
        binding.toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->

        }

        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, items)
        (binding.etCategory as? AutoCompleteTextView)?.setAdapter(adapter)

        with(binding.etDate) {
            setOnClickListener {
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(vm.date.value?.time)
                    .build().apply {
                        show(supportFragmentManager, tag)
                        addOnPositiveButtonClickListener {
                            vm.onDateChanged(it)
                        }
                    }
            }
            doOnTextChanged { _, _, _, _ -> binding.tilDate.error = null }
        }
        binding.etCategory.doOnTextChanged { text, _, _, _ ->
            binding.tilCategory.error = null
            vm.onCategoryChanged(text.toString())
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
        finish()
    }
}