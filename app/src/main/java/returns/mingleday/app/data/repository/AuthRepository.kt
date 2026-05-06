package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.AuthApi
import returns.mingleday.app.data.remote.model.auth.EmailCodeRequest
import returns.mingleday.app.data.remote.model.auth.EmailMatchRequest
import returns.mingleday.app.data.remote.model.auth.LoginRequest
import returns.mingleday.app.data.remote.model.auth.PasswordResetRequest
import returns.mingleday.app.data.remote.model.auth.Purpose
import returns.mingleday.app.data.remote.model.auth.SignupRequest
import returns.mingleday.app.data.remote.model.auth.TokenResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeApiCall
import returns.mingleday.app.data.remote.network.safeRawApiCall

class AuthRepository {

    private val authApi: AuthApi = RetrofitClient.createApi(AuthApi::class.java)

    suspend fun sendVerificationCode(
        email: String,
        purpose: Purpose
    ): ApiResult<String> {
        return safeApiCall {
            authApi.sendEmailCode(EmailCodeRequest(email, purpose))
        }
    }

    suspend fun verifyCode(
        email: String,
        code: String,
        purpose: Purpose
    ): ApiResult<String> {
        return safeApiCall {
            authApi.verifyCode(EmailMatchRequest(email, code, purpose))
        }
    }

    suspend fun signup(
        email: String,
        name: String,
        password: String,
        nickname: String
    ): ApiResult<TokenResponse> {
        return safeRawApiCall {
            authApi.signup(SignupRequest(email, name, password, nickname))
        }
    }

    suspend fun resetPassword(
        email: String,
        password: String,
    ): ApiResult<String> {
        return safeApiCall {
            authApi.resetPassword(PasswordResetRequest(email, password))
        }
    }

    suspend fun login(
        email: String,
        password: String,
    ): ApiResult<TokenResponse> {
        return safeRawApiCall {
            authApi.login(LoginRequest(email, password))
        }
    }

    suspend fun logout(): ApiResult<String> {
        return safeApiCall {
            authApi.logout()
        }
    }

    suspend fun withdraw(): ApiResult<String> {
        return safeApiCall {
            authApi.withdraw()
        }
    }
}