package returns.mingleday.app.data.remote.model.auth

data class PasswordResetRequest(
    val email: String,
    val password: String,
)