package id.petersam.dhuwite.model

data class Category(
    val category: String,
    val type: Transaction.Type
) {
    fun toEntity() = CategoryEntity(category = category, type = type.readable)
}
