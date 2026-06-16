package returns.mingleday.app.data.remote.model.schedule

data class SearchScheduleInstanceResponse(
    val mingleId: Int,
    val scheduleId: Long,
    val scheduleInstanceId: Long,
    val title: String,
    val startAt: String,
    val endAt: String,
    val memo: String,
    val scheduleStatus: ScheduleStatus
)
