package returns.mingleday.app.data.remote.model.schedule

data class CalendarDayUiModel(
    val day: Int?,
    val schedules: List<String> = emptyList(),
    val isToday: Boolean = false,
    val dayOfWeek: Int? = null
)