package returns.mingleday.app.data.remote.intercepter

import okhttp3.Interceptor
import okhttp3.Response
import returns.mingleday.app.data.local.TokenProvider

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenProvider.getAccessToken()

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}