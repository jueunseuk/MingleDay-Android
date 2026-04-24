package returns.mingleday.app.data.remote.intercepter

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import returns.mingleday.app.data.local.TokenDataStore

class AuthInterceptor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken = runBlocking {
            tokenDataStore.getAccessToken()
        }

        val newRequest = if (!accessToken.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}