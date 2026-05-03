package returns.mingleday.app.data.local

// 메모리 저장
object TokenProvider {

    private var accessToken: String? = null

    fun setAccessToken(token: String?) {
        accessToken = token
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun clear() {
        accessToken = null
    }
}