package returns.mingleday.app.data.remote.model.mingle

data class MingleMembersResponse(
    val userId: Int,
    val memberId: Long,
    val name: String,
    val profileUrl: String
)
