package id.petersam.dhuwite.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showDialog(
    title: String? = null,
    msg: String? = null,
    positiveBtn: String? = null,
    positiveBtnAction: (() -> Unit)? = null,
    negativeBtn: String? = null,
    negativeBtnAction: (() -> Unit)? = null,
) {
    MaterialAlertDialogBuilder(this)
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
