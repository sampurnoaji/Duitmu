package id.petersam.duitmu.model.entity

import androidx.room.ColumnInfo

data class CategoryChartEntity(
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "amount") val amount: Long
)
