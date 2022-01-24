package id.petersam.duitmu.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.FragmentTransactionFilterModalBinding
import id.petersam.duitmu.model.DatePeriod
import id.petersam.duitmu.util.viewBinding

class TransactionFilterModalFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentTransactionFilterModalBinding::bind)

    companion object {
        const val TAG = "TransactionFilterModalFragment"

        fun newInstance(manager: FragmentManager): TransactionFilterModalFragment? {
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

        binding.btnSave.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRadioGroup() {
        DatePeriod.values().forEach {
            val radio = RadioButton(requireContext()).apply {
                text = it.readable
                layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 0, 0,16)
                }
            }
            binding.rgFilter.addView(radio)
        }
    }
}