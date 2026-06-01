package returns.mingleday.app.data.remote.model.schedule

data class ScheduleInstanceResponse(
    val scheduleInstanceId: Long,
    val startAt: String,
    val endAt: String,
    val memo: String,
    val scheduleStatus: ScheduleStatus,
    val prev: SimpleScheduleInstanceResponse?,
    val next: SimpleScheduleInstanceResponse?
)
