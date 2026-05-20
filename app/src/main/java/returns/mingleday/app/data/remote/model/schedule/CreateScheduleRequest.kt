package returns.mingleday.app.data.remote.model.schedule

import java.time.LocalDateTime

data class CreateScheduleRequest(
    val mingleId: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val location: String,
    val categoryId: Long,
    val isRepeated: Boolean,
    val isLocked: Boolean,
    val isPrivate: Boolean,
    val isAllDay: Boolean,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val mingleMembers: List<ScheduleMemberRequest>,
    val repeatType: RepeatType,
    val repeatValue: String,
    val endType: EndType,
    val endValue: String
)
