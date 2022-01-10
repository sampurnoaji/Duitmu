package id.petersam.duitmu.util

import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
