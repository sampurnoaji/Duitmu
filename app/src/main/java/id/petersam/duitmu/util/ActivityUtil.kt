package id.petersam.duitmu.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}

fun Activity.snackBar(
    view: View,
    msg: String,
    @ColorRes color: Int? = null,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackBar = Snackbar.make(view, msg, duration)
    snackBar.setAction(actionText) { action?.invoke() }
    color?.let { snackBar.setBackgroundTint(ContextCompat.getColor(this, color)) }
    snackBar.show()
}