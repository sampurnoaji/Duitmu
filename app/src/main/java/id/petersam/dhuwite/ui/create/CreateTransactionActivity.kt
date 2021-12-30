package id.petersam.dhuwite.ui.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import id.petersam.dhuwite.R
import id.petersam.dhuwite.databinding.ActivityCreateTransactionBinding
import id.petersam.dhuwite.util.viewBinding

class CreateTransactionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCreateTransactionBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupTextInput()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupTextInput() {
        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, items)
        (binding.etCategory as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}