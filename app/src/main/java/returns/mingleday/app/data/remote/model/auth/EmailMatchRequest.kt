package returns.mingleday.app.data.remote.model.auth

data class EmailMatchRequest(
    val email: String,
    val code: String,
    val purpose: Purpose
)