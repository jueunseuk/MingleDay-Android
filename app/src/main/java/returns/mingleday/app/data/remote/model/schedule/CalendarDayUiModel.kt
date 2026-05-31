package returns.mingleday.app.data.remote.model.schedule

data class CalendarDayUiModel(
    val day: Int?,
    val schedules: List<CalendarScheduleUiModel> = emptyList(),
    val dayOfWeek: Int? = null,
    val isToday: Boolean = false,
    val anniversaryName: String? = null,
    val isHoliday: Boolean = false
)