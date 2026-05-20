package returns.mingleday.app.data.remote.model.schedule

data class UpdateScheduleRequest(
    val scheduleId: Long,
    val scheduleInstanceId: Long,
    val mingleId: Int,
    val title: String,
    val content: String,
    val location: String,
    val categoryId: Long,
    val isLocked: Boolean,
    val isPrivate: Boolean,
)
