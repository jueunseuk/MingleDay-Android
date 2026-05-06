package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.UserApi
import returns.mingleday.app.data.remote.model.user.MyPageUserResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeRawApiCall

class UserRepository {

    private val userApi: UserApi = RetrofitClient.createApi(UserApi::class.java)

    suspend fun getMyPageInfo(
    ): ApiResult<MyPageUserResponse> {
        return safeRawApiCall {
            userApi.getMyPageInfo()
        }
    }
}