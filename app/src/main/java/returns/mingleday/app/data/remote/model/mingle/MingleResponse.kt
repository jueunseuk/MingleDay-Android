package returns.mingleday.app.data.remote.model.mingle

data class MingleResponse(
    val mingleId: Int,
    val mingleName: String,
    val profileUrl: String,
    val mingleType: MingleType,
    val createdAt: String,
    val mingleMembers: List<MingleMemberWithPermissionResponse>
)