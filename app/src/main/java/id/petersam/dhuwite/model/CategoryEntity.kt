package id.petersam.dhuwite.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey val category: String,
    val type: String
) {
    fun toDomain() = Category(category = category, type = Transaction.Type.map(type))
}
