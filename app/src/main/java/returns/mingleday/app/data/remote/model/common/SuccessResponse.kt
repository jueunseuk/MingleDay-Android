package returns.mingleday.app.data.remote.model.common

data class SuccessResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val result: T?
)