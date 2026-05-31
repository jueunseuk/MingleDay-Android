package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import returns.mingleday.app.data.remote.model.schedule.AnniversaryItemWithType

interface AnniversaryApi {
    @GET("anniversary")
    suspend fun getAnniversary(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<AnniversaryItemWithType>>
}