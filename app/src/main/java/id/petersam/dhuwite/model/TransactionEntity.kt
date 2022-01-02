package id.petersam.dhuwite.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.petersam.dhuwite.util.DatePattern
import id.petersam.dhuwite.util.toDate
import java.util.Date

@Entity
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "createdAt") val createdAt: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "note") val note: String?,
    @ColumnInfo(name = "amount") val amount: Long
) {
    fun toDomain() = Transaction(
        id = createdAt,
        type = Transaction.Type.map(type),
        date = date.toDate(DatePattern.FULL) ?: Date(),
        category = category,
        note = note.orEmpty(),
        amount = amount
    )
}

