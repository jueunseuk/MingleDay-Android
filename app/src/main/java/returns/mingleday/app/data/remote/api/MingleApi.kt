package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse

interface MingleApi {
    @GET("mingles")
    suspend fun getMyMingles(): Response<List<MinglesResponse>>
}