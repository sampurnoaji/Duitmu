package id.petersam.dhuwite.model

import java.lang.IllegalArgumentException
import java.util.Date

data class Transaction(
    val id: Int,
    val type: Type,
    val date: Date,
    val category: String,
    val note: String,
    val amount: Long
) {
    enum class Type(val readable: String) {
        INCOME("income"),
        EXPENSE("expense");

        companion object {
            private val map = values().associateBy(Type::readable)
            fun map(readable: String) = map[readable] ?: throw IllegalArgumentException()
        }
    }
}
