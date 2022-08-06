package id.petersam.catatankeuangan.util

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
    FULL("yyyy-MM-dd'T'HH:mm:ssZ"),
    DRIVE("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
}

fun Date.toReadableString(pattern: DatePattern): String {
    return try {
        val formatter = SimpleDateFormat(pattern.pattern, localeIndonesia)
        formatter.format(this)
    } catch (e: Exception) {
        "Wrong Date pattern!"
    }
}

fun Date.minimizeTime(): Date {
    return Calendar.getInstance().apply {
        time = this@minimizeTime
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

fun Date.maximizeTime(): Date {
    return Calendar.getInstance().apply {
        time = this@maximizeTime
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time
}
