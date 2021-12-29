package id.petersam.dhuwite.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private val localeIndonesia: Locale = Locale("id", "ID")

enum class DatePattern(val pattern: String) {
    D("d"),
    M_LONG("MMMM"),
    Y_LONG("yyyy"),
    DMY_LONG("d MMMM yyyy"),
    FULL("yyyy-MM-dd'T'HH:mm:ss")
}

fun Date.toReadableString(pattern: DatePattern): String {
    return try {
        val formatter = SimpleDateFormat(pattern.pattern, localeIndonesia)
        formatter.format(this)
    } catch (e: Exception) {
        "Wrong Date pattern!"
    }
}