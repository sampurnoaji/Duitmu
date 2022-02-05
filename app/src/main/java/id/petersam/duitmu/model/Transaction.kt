package id.petersam.duitmu.model

import java.util.Date

data class Transaction(
    val id: String,
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

    fun toEntity() = TransactionEntity(
        createdAt = id,
        type = type.readable,
        date = date,
        category = category,
        note = note,
        amount = amount
    )
}
