package returns.mingleday.app.data.remote.api

import retrofit2.http.GET
import returns.mingleday.app.data.remote.model.schedule.SearchScheduleInstanceResponse
import returns.mingleday.app.data.remote.network.ApiResult

interface ScheduleApi {
    @GET("search")
    suspend fun searchSchedule(
    ): ApiResult<List<SearchScheduleInstanceResponse>>
}