package returns.mingleday.app.data.remote.model.auth

data class LoginRequest(
    val email: String,
    val password: String,
)