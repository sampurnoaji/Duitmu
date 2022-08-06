package id.petersam.catatankeuangan.data.google

import android.content.Context
import id.petersam.catatankeuangan.R

object Backup {

    fun getTransactionFilename(context: Context) =
        "Data_Transaksi_Aplikasi_${context.getString(R.string.app_name)}.json"

    fun getCategoryFilename(context: Context) =
        "Data_Kategori_Aplikasi_${context.getString(R.string.app_name)}.json"
}