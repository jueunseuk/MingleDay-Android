package returns.mingleday.app.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Part
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import returns.mingleday.app.data.remote.model.user.MyPageUserResponse

interface UserApi {
    @GET("users/me")
    suspend fun getMyPageInfo(): Response<MyPageUserResponse>

    @PATCH("users/profile/image")
    suspend fun updateMyProfileImage(
        @Part profileImage: MultipartBody.Part
    ): Response<SuccessResponse<String>>
}