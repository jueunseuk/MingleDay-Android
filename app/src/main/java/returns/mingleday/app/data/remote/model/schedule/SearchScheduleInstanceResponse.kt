package returns.mingleday.app.data.remote.model.schedule

data class SearchScheduleInstanceResponse(
    val mingleId: Int,
    val scheduleInstanceId: Long,
    val startAt: String,
    val endAt: String,
    val memo: String,
    val scheduleStatus: ScheduleStatus
)
