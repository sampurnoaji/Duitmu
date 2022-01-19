package id.petersam.duitmu.ui.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.room.Update
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityUpdateTransactionBinding
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.DatePattern
import id.petersam.duitmu.util.LoadState
import id.petersam.duitmu.util.addThousandSeparator
import id.petersam.duitmu.util.snackbar
import id.petersam.duitmu.util.toReadableString
import id.petersam.duitmu.util.toRupiah
import id.petersam.duitmu.util.viewBinding

@AndroidEntryPoint
class UpdateTransactionActivity : AppCompatActivity() {

    private val vm by viewModels<UpdateTransactionViewModel>()
    private val binding by viewBinding(ActivityUpdateTransactionBinding::inflate)

    companion object {
        private const val INTENT_KEY_TRX_ID = "trxId"

        @JvmStatic
        fun start(context: Context, trxId: String) {
            val starter = Intent(context, UpdateTransactionActivity::class.java)
                .putExtra(INTENT_KEY_TRX_ID, trxId)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()

        intent.extras?.let {
            val trxId = it.getString(INTENT_KEY_TRX_ID) ?: return
            vm.getTransaction(trxId)
        }

        observeTransactionResult()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun observeTransactionResult() {
        vm.trx.observe(this) {
            when (it) {
                is LoadState.Loading -> {
                    setLoading(true)
                }
                is LoadState.Success -> {
                    setLoading(false)
                    showTransactionData(it.data)
                }
                is LoadState.Error -> {
                    setLoading(false)
                    snackbar(binding.appBarLayout, getString(R.string.error_occured), R.color.red_text)
                }
            }
        }
    }

    private fun showTransactionData(trx: Transaction) {
        with(binding) {
            if (trx.type == Transaction.Type.INCOME) toggleButton.check(binding.btnIncome.id)
            else toggleButton.check(binding.btnExpense.id)

            etDate.setText(trx.date.toReadableString(DatePattern.DMY_LONG))
            etCategory.setText(trx.category)
            etAmount.setText(trx.amount.toString().addThousandSeparator())
            etNote.setText(trx.note)

            btnIncome.isEnabled = trx.type == Transaction.Type.INCOME
            btnExpense.isEnabled = trx.type == Transaction.Type.EXPENSE
        }
    }

    private fun setLoading(isVisible: Boolean) {
        binding.pgbLoading.isVisible = isVisible
        binding.btnEdit.isEnabled = !isVisible
    }
}