package returns.mingleday.app.data.remote.model.schedule

data class UpdateScheduleMemberRequest(
    val scheduleMemberId: Long,
    val memo: String
)
