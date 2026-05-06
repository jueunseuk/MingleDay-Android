package returns.mingleday.app.data.remote.model.mingle

data class MinglesResponse(
    val mingleId: Int,
    val mingleName: String,
    val profileUrl: String,
    val memberCnt: Int,
    val mingleType: MingleType,
    val createdAt: String
)