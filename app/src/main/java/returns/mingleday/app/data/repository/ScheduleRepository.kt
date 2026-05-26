package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.ScheduleApi
import returns.mingleday.app.data.remote.model.schedule.CreateScheduleRequest
import returns.mingleday.app.data.remote.model.schedule.DetailScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.SearchScheduleInstanceResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeRawApiCall

class ScheduleRepository {

    private val scheduleApi: ScheduleApi = RetrofitClient.createApi(ScheduleApi::class.java)

    suspend fun searchSchedule(
    ): ApiResult<List<SearchScheduleInstanceResponse>> {
        return safeRawApiCall {
            scheduleApi.searchSchedule()
        }
    }

    suspend fun createSchedule(
        mingleId: Int,
        createScheduleRequest: CreateScheduleRequest
    ): ApiResult<DetailScheduleResponse> {
        return safeRawApiCall {
            scheduleApi.createSchedule(mingleId, createScheduleRequest)
        }
    }
}