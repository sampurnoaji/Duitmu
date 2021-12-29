package id.petersam.dhuwite.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeIndonesia: Locale = Locale("id", "ID")

fun String.toDate(pattern: DatePattern): Date? {
    return try {
        val formatter = SimpleDateFormat(pattern.pattern, localeIndonesia)
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}