package returns.mingleday.app.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Query
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import returns.mingleday.app.data.remote.model.schedule.DailyScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.MonthlyScheduleResponse
import returns.mingleday.app.data.remote.model.user.MyPageUserResponse
import returns.mingleday.app.data.remote.model.user.UpdateProfileInfoRequest

interface UserApi {
    @GET("users/me")
    suspend fun getMyPageInfo(): Response<MyPageUserResponse>

    @Multipart
    @PATCH("users/profile/image")
    suspend fun updateMyProfileImage(
        @Part profileImage: MultipartBody.Part
    ): Response<SuccessResponse<String>>

    @PATCH("user/profile/info")
    suspend fun updateProfileInfo(
        @Body updateProfileInfo: UpdateProfileInfoRequest
    ): Response<SuccessResponse<String>>

    @GET("users/schedules/me/monthly")
    suspend fun getMyMonthlySchedules(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("keyword") keyword: String,
    ): Response<List<MonthlyScheduleResponse>>

    @GET("users/schedules/me/daily")
    suspend fun getMyDailySchedules(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int,
        @Query("keyword") keyword: String,
    ): Response<List<DailyScheduleResponse>>
}