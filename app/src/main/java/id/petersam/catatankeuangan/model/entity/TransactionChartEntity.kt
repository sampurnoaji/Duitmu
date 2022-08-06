package id.petersam.catatankeuangan.model.entity

import androidx.room.ColumnInfo
import java.util.Date

data class TransactionChartEntity(
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "amount") val amount: Long
)
