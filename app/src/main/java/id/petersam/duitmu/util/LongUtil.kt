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

/**
 * Convert Long to format short Rupiah
 * Ex : 1000000 to 1 jt
 *      25000000 to 25 jt
 *      125000 to 125 rb
 */
fun Long.toShortRupiah(): String {
    val result = when {
        this >= 1000000000 -> String.format("%.1f M", this / 1000000000.0)
        this >= 1000000 -> String.format("%.1f jt", this / 1000000.0)
        this >= 1000 -> String.format("%.0f rb", this / 1000.0)
        else -> this.toString()
    }
    return result.replace(".0", "")
}