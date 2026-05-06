package returns.mingleday.app.data.remote.model.auth

data class SignupRequest(
    val email: String,
    val name: String,
    val password: String,
    val nickname: String
)