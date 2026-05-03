package returns.mingleday.app.data.remote.network

import returns.mingleday.app.data.remote.intercepter.AuthInterceptor

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// API 생성
object RetrofitClient {

    private const val BASE_URL = "http://172.30.1.79:8080/api/v1/"

    private lateinit var retrofit: Retrofit

    fun init() {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
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