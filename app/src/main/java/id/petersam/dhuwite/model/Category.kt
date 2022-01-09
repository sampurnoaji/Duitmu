package id.petersam.dhuwite.model

data class Category(
    val id: Int = 0,
    val category: String,
    val type: Transaction.Type
) {
    fun toEntity() = CategoryEntity(id = id, category = category, type = type.readable)
}
