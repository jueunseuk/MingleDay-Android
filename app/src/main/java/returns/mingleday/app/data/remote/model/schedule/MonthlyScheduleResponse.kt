package returns.mingleday.app.data.remote.model.schedule

import returns.mingleday.app.data.remote.model.category.SimpleCategoryResponse

data class MonthlyScheduleResponse(
    val scheduleId: Long,
    val title: String,
    val content: String,
    val isRepeated: Boolean,
    val isPrivate: Boolean,
    val category: SimpleCategoryResponse,
    val scheduleInstance: SimpleScheduleInstanceResponse
)
