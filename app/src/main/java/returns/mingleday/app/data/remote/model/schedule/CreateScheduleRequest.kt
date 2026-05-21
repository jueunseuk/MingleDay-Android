package returns.mingleday.app.data.remote.model.schedule

import java.time.LocalDateTime

data class CreateScheduleRequest(
    var mingleId: Int,
    var userId: Int,
    var title: String,
    var content: String,
    var location: String,
    var categoryId: Long,
    var isRepeated: Boolean,
    var isLocked: Boolean,
    var isPrivate: Boolean,
    var isAllDay: Boolean,
    var startAt: LocalDateTime,
    var endAt: LocalDateTime,
    var mingleMembers: List<ScheduleMemberRequest>,
    var repeatType: RepeatType,
    var repeatValue: String,
    var endType: EndType,
    var endValue: String
)
