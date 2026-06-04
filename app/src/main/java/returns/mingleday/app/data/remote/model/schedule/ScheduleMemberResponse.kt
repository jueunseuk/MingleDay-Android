package returns.mingleday.app.data.remote.model.schedule

data class ScheduleMemberResponse(
    val scheduleMemberId: Long,
    val name: String,
    val memo: String,
    val profileUrl: String
)
