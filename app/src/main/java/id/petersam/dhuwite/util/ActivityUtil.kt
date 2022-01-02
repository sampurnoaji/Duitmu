package id.petersam.dhuwite.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import id.petersam.dhuwite.R

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}

fun Activity.snackbar(view: View, msg: String, @ColorRes color: Int? = null) {
    val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
    color?.let { snackbar.setBackgroundTint(ContextCompat.getColor(this, color)) }
    snackbar.show()
}