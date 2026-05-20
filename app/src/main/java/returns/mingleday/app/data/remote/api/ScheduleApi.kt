package returns.mingleday.app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
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

interface ScheduleApi {
    @GET("search")
    suspend fun searchSchedule(
    ): ApiResult<List<SearchScheduleInstanceResponse>>

    @POST("mingles/{mingleId}/schedules")
    suspend fun createSchedule(
        @Path("mingleId") mingleId: Int,
        @Body request: CreateScheduleRequest
    ): ApiResult<DetailScheduleResponse>

    @PATCH("mingles/{mingleId}/schedules/{scheduleId}")
    suspend fun updateSchedule(
        @Path("mingleId") mingleId: Int,
        @Path("scheduleId") scheduleId: Long,
        @Body request: UpdateScheduleRequest
    ): ApiResult<DetailScheduleResponse>

    @PATCH("mingles/{mingleId}/schedules/{scheduleId}/instances/{scheduleInstanceId}")
    suspend fun updateScheduleInstance(
        @Path("mingleId") mingleId: Int,
        @Path("scheduleId") scheduleId: Long,
        @Path("scheduleInstanceId") scheduleInstanceId: Long,
        @Body request: UpdateScheduleInstanceRequest
    ): ApiResult<DetailScheduleResponse>

    @PATCH("mingles/{mingleId}/schedules/{scheduleId}/instances/{scheduleInstanceId}/status")
    suspend fun updateScheduleInstanceStatus(
        @Path("mingleId") mingleId: Int,
        @Path("scheduleId") scheduleId: Long,
        @Path("scheduleInstanceId") scheduleInstanceId: Long,
        @Body status: ScheduleStatus
    ): ApiResult<SuccessResponse<String>>

    @DELETE("mingles/{mingleId}/schedules/{scheduleId}")
    suspend fun deleteSchedule(
        @Path("mingleId") mingleId: Int,
        @Path("scheduleId") scheduleId: Long
    ): ApiResult<SuccessResponse<String>>

    @PATCH("mingles/{mingleId}/schedules/{scheduleId}/members")
    suspend fun updateScheduleMember(
        @Path("mingleId") mingleId: Int,
        @Path("scheduleId") scheduleId: Long,
        @Body request: List<ScheduleMemberRequest>
    ): ApiResult<SuccessResponse<String>>

    @GET("mingles/{mingleId}/schedules/monthly")
    suspend fun getMonthlySchedules(
        @Path("mingleId") mingleId: Int,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ApiResult<List<MonthlyScheduleResponse>>

    @GET("mingles/{mingleId}/schedules/daily")
    suspend fun getDailySchedules(
        @Path("mingleId") mingleId: Int,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): ApiResult<List<DailyScheduleResponse>>

    @GET("mingles/{mingleId}/schedules/instances/{scheduleInstanceId}")
    suspend fun getScheduleDetail(
        @Path("mingleId") mingleId: Int,
        @Path("scheduleInstanceId") scheduleInstanceId: Long
    ): ApiResult<DetailScheduleResponse>
}