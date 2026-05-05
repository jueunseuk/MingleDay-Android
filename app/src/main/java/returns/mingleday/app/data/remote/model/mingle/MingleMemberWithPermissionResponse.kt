package returns.mingleday.app.data.remote.model.mingle

data class MingleMemberWithPermissionResponse(
    val memberId: Long,
    val name: String,
    val permissions: List<MinglePermissionResponse>
)
