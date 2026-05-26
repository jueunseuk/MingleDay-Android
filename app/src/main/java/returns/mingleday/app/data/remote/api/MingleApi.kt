package returns.mingleday.app.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import returns.mingleday.app.data.remote.model.mingle.CreateMingleRequest
import returns.mingleday.app.data.remote.model.mingle.CreateMingleResponse
import returns.mingleday.app.data.remote.model.mingle.InviteMingleRequest
import returns.mingleday.app.data.remote.model.mingle.MingleLogResponse
import returns.mingleday.app.data.remote.model.mingle.MingleMembersResponse
import returns.mingleday.app.data.remote.model.mingle.MinglePermissionRequest
import returns.mingleday.app.data.remote.model.mingle.MingleResponse
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse
import returns.mingleday.app.data.remote.model.mingle.MyMingleLogResponse
import returns.mingleday.app.data.remote.model.mingle.SimpleMingleResponse

interface MingleApi {
    @GET("mingles")
    suspend fun getMyMingles(): Response<List<MinglesResponse>>

    @GET("mingles/simple")
    suspend fun getMyMinglesSimple(): Response<List<SimpleMingleResponse>>

    @GET("mingles/{mingle_id}")
    suspend fun getMingle(
        @Path("mingle_id") mingleId: Int
    ): Response<MingleResponse>

    @GET("mingle/{mingleId}/members")
    suspend fun getMingleMembers(
        @Path("mingleId") mingleId: Int
    ): Response<List<MingleMembersResponse>>

    @POST("mingles")
    suspend fun createMingle(
        @Body createMingleRequest: CreateMingleRequest
    ): Response<CreateMingleResponse>

    @POST("mingles/invitation")
    suspend fun inviteMingle(
        @Body inviteMingleRequest: InviteMingleRequest
    ): Response<SuccessResponse<String>>

    @PATCH("mingles/{mingleId}/setting")
    suspend fun updateSetting(
        @Path("mingleId") mingleId: Int,
        @Query("option") option: String,
        @Query("value") value: Boolean
    ): Response<SuccessResponse<String>>

    @PATCH("mingles/{mingleId}/members/{mingleMemberId}")
    suspend fun updateMemberPermission(
        @Path("mingleId") mingleId: Int,
        @Path("mingleMemberId") mingleMemberId: Long,
        @Body minglePermissionRequest: MinglePermissionRequest
    ): Response<SuccessResponse<String>>

    @DELETE("mingles/{mingleId}/members/leave")
    suspend fun leaveMingle(
        @Path("mingleId") mingleId: Int
    ): Response<SuccessResponse<String>>

    @GET("mingles/logs/me")
    suspend fun getMyLogs(): Response<List<MyMingleLogResponse>>

    @GET("mingles/{mingleId}/logs")
    suspend fun getMingleLogs(
        @Path("mingleId") mingleId: Int
    ): Response<List<MingleLogResponse>>

    @Multipart
    @PATCH("mingles/{mingleId}/profile")
    suspend fun updateMingleImage(
        @Path("mingleId") mingleId: Int,
        @Part mingleImage: MultipartBody.Part
    ): Response<SuccessResponse<String>>
}