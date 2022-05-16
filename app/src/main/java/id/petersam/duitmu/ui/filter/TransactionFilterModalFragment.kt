package id.petersam.duitmu.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Pair
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.FragmentTransactionFilterModalBinding
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.ui.main.MainViewModel
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.viewBinding
import java.util.Calendar

@AndroidEntryPoint
class TransactionFilterModalFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentTransactionFilterModalBinding::bind)
    private val vm by activityViewModels<MainViewModel>()

    companion object {
        const val TAG = "TransactionFilterModalFragment"

        fun newInstance(
            manager: FragmentManager
        ): TransactionFilterModalFragment? {
            manager.findFragmentByTag(TAG) ?: return TransactionFilterModalFragment()
            return null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_filter_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioGroup()
        setupActionView()

        observeDate()
    }

    private fun observeDate() {
        vm.startDate.observe(this) {
            binding.etStartDate.setText(it?.toReadableString(DatePattern.DMY_SHORT))
        }
        vm.endDate.observe(this) {
            binding.etEndDate.setText(it?.toReadableString(DatePattern.DMY_SHORT))
        }
    }

    private fun setupActionView() {
        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val radioId = binding.rgFilter.checkedRadioButtonId
            val filter = binding.rgFilter.findViewById<RadioButton>(radioId).text.toString()
            vm.onDatePeriodChanged(filter)
            validateInput()
        }

        binding.rgFilter.setOnCheckedChangeListener { _, _ ->
            removeEditTextError()
            val radioId = binding.rgFilter.checkedRadioButtonId
            val filter = binding.rgFilter.findViewById<RadioButton>(radioId).text.toString()
            if (filter == DatePeriod.CUSTOM.readable) {
                showCustomDatePeriodPicker()
                setEditTextEnabled(true)
            } else {
                setEditTextEnabled(false)
                vm.onStartDateChanged(null)
                vm.onEndDateChanged(null)
            }
        }

        binding.etStartDate.apply {
            doOnTextChanged { _, _, _, _ -> removeEditTextError() }
            setOnClickListener { showCustomDatePeriodPicker() }
        }

        binding.etEndDate.setOnClickListener { showCustomDatePeriodPicker() }
    }

    private fun setupRadioGroup() {
        DatePeriod.values().forEach {
            val radio = RadioButton(requireContext()).apply {
                text = it.readable
                layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            }
            binding.rgFilter.addView(radio)

            if (vm.datePeriod.value?.readable == it.readable) binding.rgFilter.check(radio.id)
        }
    }

    private fun showCustomDatePeriodPicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
            .setSelection(Pair(vm.startDate.value?.time, vm.endDate.value?.time))
            .build()
        picker.apply {
            isCancelable = false
            addOnPositiveButtonClickListener {
                selectCustomDatePeriod(it)
            }
        }
        picker.show(childFragmentManager, tag)
    }

    private fun selectCustomDatePeriod(pair: Pair<Long, Long>) {
        vm.onStartDateChanged(Calendar.getInstance().apply {
            timeInMillis = pair.first
        }.time)
        vm.onEndDateChanged(Calendar.getInstance().apply {
            timeInMillis = pair.second
        }.time)
    }

    private fun validateInput() {
        if (vm.datePeriod.value == DatePeriod.CUSTOM
            && binding.etStartDate.text.toString().isEmpty()) {
            binding.etStartDate.error = getString(R.string.error_required_field)
            binding.etEndDate.error = getString(R.string.error_required_field)
        } else {
            vm.getTransactions()
            vm.updateChartData()
            dismiss()
        }
    }

    private fun setEditTextEnabled(isEnabled: Boolean) {
        binding.etStartDate.isEnabled = isEnabled
        binding.etEndDate.isEnabled = isEnabled
    }

    private fun removeEditTextError() {
        binding.etStartDate.error = null
        binding.etEndDate.error = null
    }
}