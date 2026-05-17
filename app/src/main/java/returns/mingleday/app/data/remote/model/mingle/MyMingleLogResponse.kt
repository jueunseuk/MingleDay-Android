package returns.mingleday.app.data.remote.model.mingle

data class MyMingleLogResponse(
    val mingleLogId: Long,
    val operatorId: Long,
    val operatorName: String,
    val targetType: TargetType,
    val targetId: Long,
    val targetName: String,
    val content: String,
    val mingleLogType: MingleLogType,
    val createdAt: String
)
