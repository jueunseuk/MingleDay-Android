package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import returns.mingleday.app.data.remote.model.auth.EmailCodeRequest
import returns.mingleday.app.data.remote.model.auth.SignupRequest
import returns.mingleday.app.data.remote.model.auth.TokenResponse
import returns.mingleday.app.data.remote.model.auth.EmailMatchRequest
import returns.mingleday.app.data.remote.model.auth.LoginRequest
import returns.mingleday.app.data.remote.model.auth.PasswordResetRequest
import returns.mingleday.app.data.remote.model.common.SuccessResponse

interface AuthApi {
    @POST("auth/email/codes")
    suspend fun sendEmailCode(
        @Body request: EmailCodeRequest
    ): Response<SuccessResponse<String>>

    @POST("auth/email/codes/verify")
    suspend fun verifyCode(
        @Body request: EmailMatchRequest
    ): Response<SuccessResponse<String>>

    @POST("auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<TokenResponse>

    @PATCH("auth/password/reset")
    suspend fun resetPassword(
        @Body request: PasswordResetRequest
    ): Response<SuccessResponse<String>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<SuccessResponse<String>>
}