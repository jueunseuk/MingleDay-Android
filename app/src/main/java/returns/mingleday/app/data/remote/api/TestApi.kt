package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import returns.mingleday.app.data.remote.model.auth.SimpleUserResponse

interface TestApi {
    @GET("test/ping")
    suspend fun ping(): Response<String>

    @GET("test/me")
    suspend fun getMyInfo(): Response<SimpleUserResponse>
}