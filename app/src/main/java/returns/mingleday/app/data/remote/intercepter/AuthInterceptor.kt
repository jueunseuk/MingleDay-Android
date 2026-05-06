package returns.mingleday.app.data.remote.intercepter

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import returns.mingleday.app.data.local.TokenDataStore
import returns.mingleday.app.data.local.TokenProvider

class AuthInterceptor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenProvider.getAccessToken()

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            TokenProvider.clear()

            runBlocking {
                tokenDataStore.clearTokens()
            }

            SessionManager.logout()
        }

        return response
    }
}