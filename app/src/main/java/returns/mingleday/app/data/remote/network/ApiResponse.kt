package returns.mingleday.app.data.remote.network

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T
    ) : ApiResult<T>()

    data class Error(
        val code: String?,
        val message: String
    ) : ApiResult<Nothing>()

    data class Exception(
        val throwable: Throwable,
        val message: String
    ) : ApiResult<Nothing>()
}