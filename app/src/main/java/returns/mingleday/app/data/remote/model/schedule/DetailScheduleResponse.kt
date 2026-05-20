package returns.mingleday.app.data.remote.model.schedule

import returns.mingleday.app.data.remote.model.category.CategoryResponse

data class DetailScheduleResponse(
    val scheduleId: Long,
    val owner: Unit, // 서버 수정 필요. User 반환하는 코드 바꿔야 함
    val title: String,
    val content: String,
    val location: String,
    val isRepeated: Boolean,
    val isLocked: Boolean,
    val isPrivate: Boolean,
    val category: CategoryResponse,
    val members: List<ScheduleMemberResponse>,
    val scheduleInstance: ScheduleInstanceResponse
)
