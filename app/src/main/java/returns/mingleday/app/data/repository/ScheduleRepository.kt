package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.ScheduleApi
import returns.mingleday.app.data.remote.model.schedule.SearchScheduleInstanceResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient

class ScheduleRepository {

    private val scheduleApi: ScheduleApi = RetrofitClient.createApi(ScheduleApi::class.java)

    suspend fun searchSchedule(
    ): ApiResult<List<SearchScheduleInstanceResponse>> {
        return scheduleApi.searchSchedule()
    }
}