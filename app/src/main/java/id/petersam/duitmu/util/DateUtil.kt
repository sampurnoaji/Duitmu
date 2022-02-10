package id.petersam.duitmu.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


private val localeIndonesia: Locale = Locale("id", "ID")

enum class DatePattern(val pattern: String) {
    D("d"),
    MY_LONG("MMMM yyyy"),
    DMY_SHORT("d MMM yyyy"),
    DMY_LONG("d MMMM yyyy"),
    YMD("yyyy-MM-dd"),
    FULL("yyyy-MM-dd'T'HH:mm:ssZ")
}

fun Date.toReadableString(pattern: DatePattern): String {
    return try {
        val formatter = SimpleDateFormat(pattern.pattern, localeIndonesia)
        formatter.format(this)
    } catch (e: Exception) {
        "Wrong Date pattern!"
    }
}

fun Date.removeTime(): Date {
    return Calendar.getInstance().apply {
        time = this@removeTime
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}