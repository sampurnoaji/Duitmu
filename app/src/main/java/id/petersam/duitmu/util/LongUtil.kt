package id.petersam.duitmu.util

import java.util.*

fun Long.toRupiah(): String {
    return try {
        val localeId = Locale("id", "ID")
        "Rp " + String.format(localeId, "%,d", this)
    } catch (e: Exception) {
        "-"
    }
}