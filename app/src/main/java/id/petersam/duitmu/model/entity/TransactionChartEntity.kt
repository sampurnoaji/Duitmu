package id.petersam.duitmu.model.entity

import androidx.room.ColumnInfo

data class TransactionChartEntity(
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "amount") val amount: Long
)
