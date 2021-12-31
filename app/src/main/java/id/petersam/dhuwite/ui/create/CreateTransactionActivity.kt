package id.petersam.dhuwite.ui.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.dhuwite.R
import id.petersam.dhuwite.databinding.ActivityCreateTransactionBinding
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.toReadableString
import id.petersam.dhuwite.util.viewBinding
import java.util.Calendar

@AndroidEntryPoint
class CreateTransactionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCreateTransactionBinding::inflate)
    private val vm by viewModels<CreateTransactionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupTextInput()
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

    private fun setupTextInput() {
        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, items)
        (binding.etCategory as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.etDate.setOnClickListener {
            MaterialDatePicker.Builder.datePicker()
                .setSelection(vm.date.value?.time)
                .build().apply {
                    show(supportFragmentManager, tag)
                    addOnPositiveButtonClickListener {
                        vm.onDateChanged(it)
                    }
                }
        }
    }
}