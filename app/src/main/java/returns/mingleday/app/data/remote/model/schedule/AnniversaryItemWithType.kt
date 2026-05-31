package returns.mingleday.app.data.remote.model.schedule

data class AnniversaryItemWithType(
    val dateKind: Int,
    val dateName: String,
    val isHoliday: Boolean,
    val locdate: String,
    val seq: Int
)
