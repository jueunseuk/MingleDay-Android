package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.AnniversaryApi
import returns.mingleday.app.data.remote.model.schedule.AnniversaryItemWithType
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeRawApiCall

class AnniversaryRepository {

    private val anniversaryApi: AnniversaryApi = RetrofitClient.createApi(AnniversaryApi::class.java)

    suspend fun getAnniversary(
        year: Int,
        month: Int
    ): ApiResult<List<AnniversaryItemWithType>> {
        return safeRawApiCall {
            anniversaryApi.getAnniversary(year, month)
        }
    }
}