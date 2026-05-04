package returns.mingleday.app.data.remote.network

import android.content.Context
import returns.mingleday.app.data.remote.intercepter.AuthInterceptor

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import returns.mingleday.app.MingleDayApplication

// API 생성
object RetrofitClient {

    private const val BASE_URL = "http://returns.ddns.net:8080/api/v1/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val app = context.applicationContext as MingleDayApplication
        val tokenDataStore = app.tokenDataStore

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenDataStore))
            .addInterceptor(loggingInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createApi(service: Class<T>): T {
        check(::retrofit.isInitialized) {
            "RetrofitClient.init() must be called first"
        }

        return retrofit.create(service)
    }
}