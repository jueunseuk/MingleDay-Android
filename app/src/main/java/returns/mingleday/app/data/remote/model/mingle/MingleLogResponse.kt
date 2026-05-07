package returns.mingleday.app.data.remote.model.mingle

data class MingleLogResponse(
    val mingleLogId: Long,
    val mingleId: Int,
    val operatorId: Long,
    val operatorName: String,
    val targetType: TargetType,
    val targetId: Long,
    val targetName: String,
    val content: String,
    val mingleLogType: MingleLogType,
    val createdAt: String
)
