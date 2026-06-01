package returns.mingleday.app.data.remote.model.user

data class UpdateProfileInfoRequest(
    val userId: Int,
    val name: String,
    val nickname: String
)
