package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.MingleApi
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeRawApiCall

class MingleRepository {

    private val mingleApi: MingleApi = RetrofitClient.createApi(MingleApi::class.java)

    suspend fun getMyPageInfo(
    ): ApiResult<List<MinglesResponse>> {
        return safeRawApiCall {
            mingleApi.getMyMingles()
        }
    }
}