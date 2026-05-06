package returns.mingleday.app.data.remote.model.common

data class ErrorResponse(
    val success: Boolean?,
    val code: String?,
    val message: String?
)