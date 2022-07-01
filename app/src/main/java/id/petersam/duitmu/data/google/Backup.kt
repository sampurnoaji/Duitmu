package id.petersam.duitmu.data.google

import android.content.Context
import id.petersam.duitmu.R

object Backup {

    fun getTransactionFilename(context: Context) =
        "Data Transaksi Aplikasi ${context.getString(R.string.app_name)}"

    fun getCategoryFilename(context: Context) =
        "Data Kategori Aplikasi ${context.getString(R.string.app_name)}"
}