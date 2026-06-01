package returns.mingleday.app.data.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import returns.mingleday.app.data.remote.api.UserApi
import returns.mingleday.app.data.remote.model.schedule.DailyScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.MonthlyScheduleResponse
import returns.mingleday.app.data.remote.model.user.MyPageUserResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeApiCall
import returns.mingleday.app.data.remote.network.safeRawApiCall
import java.io.File

class UserRepository {

    private val userApi: UserApi = RetrofitClient.createApi(UserApi::class.java)

    suspend fun getMyPageInfo(
    ): ApiResult<MyPageUserResponse> {
        return safeRawApiCall {
            userApi.getMyPageInfo()
        }
    }

    suspend fun updateMyProfileImage(
        profileImage: File
    ): ApiResult<String> {
        return safeApiCall {
            userApi.updateMyProfileImage(
                MultipartBody.Part.createFormData(
                    "profileImage",
                    profileImage.name,
                    profileImage.asRequestBody("image/*".toMediaType())
                )
            )
        }
    }

    suspend fun getMyMonthlySchedules(
        year: Int,
        month: Int,
        keyword: String
    ): ApiResult<List<MonthlyScheduleResponse>> {
        return safeRawApiCall {
            userApi.getMyMonthlySchedules(year, month, keyword)
        }
    }

    suspend fun getMyDailySchedules(
        year: Int,
        month: Int,
        day: Int,
        keyword: String
    ): ApiResult<List<DailyScheduleResponse>> {
        return safeRawApiCall {
            userApi.getMyDailySchedules(year, month, day, keyword)
        }
    }
}