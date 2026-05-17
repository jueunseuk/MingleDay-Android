package returns.mingleday.app.data.remote.model.category

data class UpsertCategoryRequest(
    val categoryId: Long?,
    val mingleId: Int,
    val name: String,
    val description: String,
    val backgroundColor: String,
    val textColor: String
)
