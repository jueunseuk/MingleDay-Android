package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import returns.mingleday.app.data.remote.model.auth.EmailCodeRequest
import returns.mingleday.app.data.remote.model.common.SuccessResponse

interface EmailApi {

    @POST("email/codes")
    suspend fun sendEmailCode(
        @Body request: EmailCodeRequest
    ): Response<SuccessResponse<String>>
}