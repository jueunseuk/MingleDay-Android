package returns.mingleday.app.data.remote.model.schedule

import java.time.LocalDateTime

data class UpdateScheduleInstanceRequest(
    val scheduleInstanceId: Long,
    val scheduleId: Long,
    val mingleId: Int,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val memo: String
)
