package id.petersam.dhuwite.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.petersam.dhuwite.R

fun Context.showDialog(
    view: View? = null,
    title: String? = null,
    msg: String? = null,
    positiveBtn: String? = null,
    positiveBtnAction: (() -> Unit)? = null,
    negativeBtn: String? = null,
    negativeBtnAction: (() -> Unit)? = null,
) {
    MaterialAlertDialogBuilder(this)
        .setView(view)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton(positiveBtn) { dialog, which ->
            positiveBtnAction?.invoke()
        }
        .setNegativeButton(negativeBtn) { dialog, which ->
            negativeBtnAction?.invoke()
        }
        .show()
}
