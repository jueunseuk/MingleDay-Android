package returns.mingleday.app.data.remote.model.schedule

data class SimpleScheduleInstanceResponse(
    val scheduleInstanceId: Long,
    val startAt: String,
    val endAt: String
)
