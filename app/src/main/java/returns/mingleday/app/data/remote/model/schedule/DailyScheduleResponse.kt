package returns.mingleday.app.data.remote.model.schedule

import returns.mingleday.app.data.remote.model.category.CategoryResponse

data class DailyScheduleResponse(
    val scheduleId: Long,
    val mingleId: Int,
    val title: String,
    val content: String,
    val isRepeated: Boolean,
    val isPrivate: Boolean,
    val category: CategoryResponse,
    val scheduleInstance: ScheduleInstanceResponse
)
