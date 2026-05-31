package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.ScheduleApi
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import returns.mingleday.app.data.remote.model.schedule.CreateScheduleRequest
import returns.mingleday.app.data.remote.model.schedule.DailyScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.DetailScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.MonthlyScheduleResponse
import returns.mingleday.app.data.remote.model.schedule.ScheduleMemberRequest
import returns.mingleday.app.data.remote.model.schedule.ScheduleStatus
import returns.mingleday.app.data.remote.model.schedule.SearchScheduleInstanceResponse
import returns.mingleday.app.data.remote.model.schedule.UpdateScheduleInstanceRequest
import returns.mingleday.app.data.remote.model.schedule.UpdateScheduleRequest
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

    suspend fun updateSchedule(
        mingleId: Int,
        scheduleId: Long,
        updateScheduleRequest: UpdateScheduleRequest
    ): ApiResult<DetailScheduleResponse> {
        return safeRawApiCall {
            scheduleApi.updateSchedule(mingleId, scheduleId, updateScheduleRequest)
        }
    }

    suspend fun updateScheduleInstance(
        mingleId: Int,
        scheduleId: Long,
        scheduleInstanceId: Long,
        updateScheduleInstanceRequest: UpdateScheduleInstanceRequest
    ): ApiResult<DetailScheduleResponse> {
        return safeRawApiCall {
            scheduleApi.updateScheduleInstance(
                mingleId,
                scheduleId,
                scheduleInstanceId,
                updateScheduleInstanceRequest
            )
        }
    }

    suspend fun updateScheduleInstanceStatus(
        mingleId: Int,
        scheduleId: Long,
        scheduleInstanceId: Long,
        status: ScheduleStatus
    ): ApiResult<SuccessResponse<String>> {
        return safeRawApiCall {
            scheduleApi.updateScheduleInstanceStatus(
                mingleId,
                scheduleId,
                scheduleInstanceId,
                status
            )
        }
    }

    suspend fun deleteSchedule(
        mingleId: Int,
        scheduleId: Long
    ): ApiResult<SuccessResponse<String>> {
        return safeRawApiCall {
            scheduleApi.deleteSchedule(mingleId, scheduleId)
        }
    }

    suspend fun updateScheduleMember(
        mingleId: Int,
        scheduleId: Long,
        request: List<ScheduleMemberRequest>
    ): ApiResult<SuccessResponse<String>> {
        return safeRawApiCall {
            scheduleApi.updateScheduleMember(mingleId, scheduleId, request)
        }
    }

    suspend fun getMonthlySchedules(
        mingleId: Int,
        year: Int,
        month: Int
    ): ApiResult<List<MonthlyScheduleResponse>> {
        return safeRawApiCall {
            scheduleApi.getMonthlySchedules(mingleId, year, month)
        }
    }

    suspend fun getDailySchedules(
        mingleId: Int,
        year: Int,
        month: Int,
        day: Int
    ): ApiResult<List<DailyScheduleResponse>> {
        return safeRawApiCall {
            scheduleApi.getDailySchedules(mingleId, year, month, day)
        }
    }

    suspend fun getScheduleDetail(
        mingleId: Int,
        scheduleInstanceId: Long
    ): ApiResult<DetailScheduleResponse> {
        return safeRawApiCall {
            scheduleApi.getScheduleDetail(mingleId, scheduleInstanceId)
        }
    }
}