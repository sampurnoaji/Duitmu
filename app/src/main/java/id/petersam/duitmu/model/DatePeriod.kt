package id.petersam.duitmu.model

import java.util.Calendar
import java.util.Date

enum class DatePeriod(
    val readable: String,
    val startDate: Date? = null,
    val endDate: Date? = null
) {
    ALL("Semua"),
    TODAY("Hari ini", Date()),
    CURRENT_MONTH("Bulan ini", getFirstDateOfCurrentMonth(), getEndDateOfCurrentMonth()),
    CURRENT_YEAR("Tahun ini", getFirstDateOfCurrentYear(), getEndDateOfCurrentYear())
}

fun getFirstDateOfCurrentMonth(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.time
}

fun getEndDateOfCurrentMonth(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DATE))
    }.time
}

fun getFirstDateOfCurrentYear(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_YEAR, 1)
    }.time
}

fun getEndDateOfCurrentYear(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.MONTH, 11)
        set(Calendar.DAY_OF_MONTH, 31)
    }.time
}