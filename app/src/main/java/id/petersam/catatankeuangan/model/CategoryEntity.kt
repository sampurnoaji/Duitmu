package id.petersam.catatankeuangan.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "type") val type: String
) {
    fun toDomain() = Category(id = id, category = category, type = Transaction.Type.map(type))
}
