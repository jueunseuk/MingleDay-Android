package returns.mingleday.app.data.remote.network

import returns.mingleday.app.data.local.TokenDataStore
import returns.mingleday.app.data.remote.intercepter.AuthInterceptor

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://returns.ddns.net:8080/api/v1/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val tokenDataStore = TokenDataStore(context)

        val authInterceptor = AuthInterceptor(tokenDataStore)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createApi(service: Class<T>): T {
        return retrofit.create(service)
    }
}