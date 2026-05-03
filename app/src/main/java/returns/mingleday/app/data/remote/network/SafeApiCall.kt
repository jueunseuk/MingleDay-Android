package returns.mingleday.app.data.remote.network

import com.google.gson.Gson
import retrofit2.Response
import returns.mingleday.app.data.remote.model.common.ErrorResponse
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import java.io.IOException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<SuccessResponse<T>>
): ApiResult<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
                ?: return ApiResult.Error(
                    code = response.code().toString(),
                    message = "응답 데이터가 비어 있습니다."
                )

            if (!body.success) {
                return ApiResult.Error(
                    code = body.code,
                    message = body.message
                )
            }

            val result = body.result
                ?: return ApiResult.Error(
                    code = body.code,
                    message = "응답 result가 비어 있습니다."
                )

            ApiResult.Success(result)
        } else {
            ApiResult.Error(
                code = response.code().toString(),
                message = parseErrorMessage(response)
            )
        }
    } catch (e: IOException) {
        ApiResult.Exception(e, "네트워크 연결을 확인해주세요.")
    } catch (e: Exception) {
        ApiResult.Exception(e, e.message ?: "알 수 없는 오류가 발생했습니다.")
    }
}

suspend fun <T> safeRawApiCall(
    apiCall: suspend () -> Response<T>
): ApiResult<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
                ?: return ApiResult.Error(
                    code = response.code().toString(),
                    message = "응답 데이터가 비어 있습니다."
                )

            ApiResult.Success(body)
        } else {
            ApiResult.Error(
                code = response.code().toString(),
                message = parseErrorMessage(response)
            )
        }
    } catch (e: IOException) {
        ApiResult.Exception(e, "네트워크 연결을 확인해주세요.")
    } catch (e: Exception) {
        ApiResult.Exception(e, e.message ?: "알 수 없는 오류가 발생했습니다.")
    }
}

private fun parseErrorMessage(response: Response<*>): String {
    return try {
        val errorJson = response.errorBody()?.string()

        if (errorJson.isNullOrBlank()) {
            "서버 오류가 발생했습니다."
        } else {
            val errorResponse = Gson().fromJson(
                errorJson,
                ErrorResponse::class.java
            )

            errorResponse.message ?: "서버 오류가 발생했습니다."
        }
    } catch (e: Exception) {
        "서버 오류가 발생했습니다."
    }
}