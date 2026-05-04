package returns.mingleday.app.data.remote.model.user

import returns.mingleday.app.data.remote.model.auth.Role

data class MyPageUserResponse(
    val userId: Int,
    val name: String,
    val nickname: String,
    val email: String,
    val profileUrl: String,
    val createdAt: String,
    val passwordUpdatedAt: String,
    val birthday: String,
    val role: Role,
    val belongMingleCnt: Int
)