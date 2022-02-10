package id.petersam.duitmu.model

import java.util.Calendar
import java.util.Date

enum class DatePeriod(
    val readable: String,
    val startDate: Date? = null,
    val endDate: Date? = null
) {
    ALL("Semua"),
    TODAY("Hari ini", getStartToday(), getEndToday()),
    CURRENT_MONTH("Bulan ini", getFirstDateOfCurrentMonth(), getEndDateOfCurrentMonth()),
    CURRENT_YEAR("Tahun ini", getFirstDateOfCurrentYear(), getEndDateOfCurrentYear()),
    CUSTOM("Pilih tanggal dari kalender")
}

private fun todayStartCalendar(): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
}

private fun todayEndCalendar(): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
}

fun getStartToday(): Date = todayStartCalendar().time
fun getEndToday(): Date = todayEndCalendar().time

fun getFirstDateOfCurrentMonth(): Date {
    return todayStartCalendar().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.time
}

fun getEndDateOfCurrentMonth(): Date {
    return todayEndCalendar().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DATE))
    }.time
}

fun getFirstDateOfCurrentYear(): Date {
    return todayStartCalendar().apply {
        set(Calendar.DAY_OF_YEAR, 1)
    }.time
}

fun getEndDateOfCurrentYear(): Date {
    return todayEndCalendar().apply {
        set(Calendar.MONTH, 11)
        set(Calendar.DAY_OF_MONTH, 31)
    }.time
}