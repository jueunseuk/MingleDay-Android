package returns.mingleday.app.data.remote.model.category

data class CategoryResponse(
    val categoryId: Long,
    val name: String,
    val description: String,
    val backgroundColor: String,
    val textColor: String
)
