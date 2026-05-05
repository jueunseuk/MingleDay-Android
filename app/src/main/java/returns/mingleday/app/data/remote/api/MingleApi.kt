package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import returns.mingleday.app.data.remote.model.mingle.CreateMingleRequest
import returns.mingleday.app.data.remote.model.mingle.CreateMingleResponse
import returns.mingleday.app.data.remote.model.mingle.InviteMingleRequest
import returns.mingleday.app.data.remote.model.mingle.MingleResponse
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse

interface MingleApi {
    @GET("mingles")
    suspend fun getMyMingles(): Response<List<MinglesResponse>>

    @GET("mingles/{mingle_id}")
    suspend fun getMingle(
        @Path("mingle_id") mingleId: Int
    ): Response<MingleResponse>

    @POST("mingles")
    suspend fun createMingle(
        @Body createMingleRequest: CreateMingleRequest
    ): Response<CreateMingleResponse>

    @POST("mingles/invitation")
    suspend fun inviteMingle(
        @Body inviteMingleRequest: InviteMingleRequest
    ): Response<SuccessResponse<String>>
}