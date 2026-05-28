package returns.mingleday.app.data.remote.model.schedule

import java.time.LocalDate

data class AnniversaryItemWithType(
    val dateKind: Int,
    val dateName: String,
    val isHoliday: Boolean,
    val locdate: LocalDate,
    val seq: Int
)
