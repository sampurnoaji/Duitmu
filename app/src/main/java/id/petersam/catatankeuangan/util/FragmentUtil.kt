package id.petersam.catatankeuangan.util

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackBar(
    msg: String,
    @ColorRes color: Int? = null,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackBar = Snackbar.make(requireView(), msg, duration)
    snackBar.setAction(actionText) { action?.invoke() }
    color?.let { snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), color)) }
    snackBar.show()
}
