package returns.mingleday.app.data.remote.model.schedule

import returns.mingleday.app.data.remote.model.user.ScheduleStatus

data class SearchScheduleInstanceResponse(
    val mingleId: Int,
    val scheduleInstanceId: Long,
    val startAt: String,
    val endAt: String,
    val memo: String,
    val scheduleStatus: ScheduleStatus
)
