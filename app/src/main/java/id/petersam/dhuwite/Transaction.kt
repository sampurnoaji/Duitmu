package id.petersam.dhuwite

import java.util.*

data class Transaction(
    val id: Int,
    val type: Type,
    val date: Date,
    val category: String,
    val note: String,
    val amount: Long
) {
    enum class Type { INCOME, EXPENSE }
}
