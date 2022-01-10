package id.petersam.duitmu.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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

fun String.addThousandSeparator(): String {
    return try {
        val number = this.toLong()
        val symbol = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###,###", symbol)
        return formatter.format(number)
    } catch (e: Exception) {
        this
    }
}

fun String.removeThousandSeparator(): Long? {
    return try {
        this.replace(".", "").toLong()
    } catch (e: Exception) {
        null
    }
}