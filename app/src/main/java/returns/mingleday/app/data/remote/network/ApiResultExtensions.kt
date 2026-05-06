package returns.mingleday.app.data.remote.network

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (String) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(message)
    return this
}

inline fun <T> ApiResult<T>.onException(action: (String) -> Unit): ApiResult<T> {
    if (this is ApiResult.Exception) action(message)
    return this
}