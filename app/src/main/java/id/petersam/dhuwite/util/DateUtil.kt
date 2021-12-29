package id.petersam.dhuwite.util

import java.text.SimpleDateFormat
import java.util.*

private val localeIndonesia: Locale = Locale("id", "ID")

enum class DatePattern(val pattern: String) {
    D("d"),
    M_LONG("MMMM"),
    Y_LONG("yyyy")
}

fun Date.toReadableString(pattern: DatePattern): String {
    return try {
        val formatter = SimpleDateFormat(pattern.pattern, localeIndonesia)
        formatter.format(this)
    } catch (e: Exception) {
        "Wrong Date pattern!"
    }
}