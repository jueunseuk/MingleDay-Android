package returns.mingleday.app.data.remote.model.auth

data class EmailCodeRequest(
    val email: String,
    val purpose: Purpose
)