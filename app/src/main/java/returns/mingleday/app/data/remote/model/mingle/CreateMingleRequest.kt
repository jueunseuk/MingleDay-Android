package returns.mingleday.app.data.remote.model.mingle

data class CreateMingleRequest(
    val name: String,
    val description: String,
    val usePermission: Boolean,
    val useRealname: Boolean,
    val mingleType: MingleType
)