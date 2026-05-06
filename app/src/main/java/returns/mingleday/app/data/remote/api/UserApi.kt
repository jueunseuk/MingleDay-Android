package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import returns.mingleday.app.data.remote.model.user.MyPageUserResponse

interface UserApi {
    @GET("users/me")
    suspend fun getMyPageInfo(): Response<MyPageUserResponse>
}