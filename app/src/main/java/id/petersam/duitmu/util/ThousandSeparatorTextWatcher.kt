package id.petersam.duitmu.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ThousandSeparatorTextWatcher(
    private val editText: EditText
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        editText.removeTextChangedListener(this)
        val formattedInput = s.toString().removeThousandSeparator()
        formattedInput?.let {
            editText.setText(formattedInput.toString().addThousandSeparator())
            editText.setSelection(editText.text.length)
        }
        editText.addTextChangedListener(this)
    }
}