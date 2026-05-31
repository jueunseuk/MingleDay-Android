package returns.mingleday.app.data.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import returns.mingleday.app.data.remote.api.MingleApi
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
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeApiCall
import returns.mingleday.app.data.remote.network.safeRawApiCall
import java.io.File

class MingleRepository {

    private val mingleApi: MingleApi = RetrofitClient.createApi(MingleApi::class.java)

    suspend fun getMyMingles(
    ): ApiResult<List<MinglesResponse>> {
        return safeRawApiCall {
            mingleApi.getMyMingles()
        }
    }

    suspend fun getMyMinglesSimple(
    ): ApiResult<List<SimpleMingleResponse>> {
        return safeRawApiCall {
            mingleApi.getMyMinglesSimple()
        }
    }

    suspend fun getMingle(
        mingleId: Int
    ): ApiResult<MingleResponse> {
        return safeRawApiCall {
            mingleApi.getMingle(mingleId)
        }
    }

    suspend fun getMingleMembers(
        mingleId: Int
    ): ApiResult<List<MingleMembersResponse>> {
        return safeRawApiCall {
            mingleApi.getMingleMembers(mingleId)
        }
    }

    suspend fun createMingle(
        createMingleRequest: CreateMingleRequest
    ): ApiResult<CreateMingleResponse> {
        return safeRawApiCall {
            mingleApi.createMingle(createMingleRequest)
        }
    }

    suspend fun inviteMingle(
        mingleId: Int,
        email: String
    ): ApiResult<String> {
        return safeApiCall {
            mingleApi.inviteMingle(InviteMingleRequest(mingleId, email))
        }
    }

    suspend fun updateSetting(
        mingleId: Int,
        option: String,
        value: Boolean
    ): ApiResult<String> {
        return safeApiCall {
            mingleApi.updateSetting(mingleId, option, value)
        }
    }

    suspend fun updateMemberPermission(
        mingleId: Int,
        mingleMemberId: Long,
        minglePermissionRequest: MinglePermissionRequest
    ): ApiResult<String> {
        return safeApiCall {
            mingleApi.updateMemberPermission(mingleId, mingleMemberId, minglePermissionRequest)
        }
    }

    suspend fun leaveMingle(
        mingleId: Int
    ): ApiResult<String> {
        return safeApiCall {
            mingleApi.leaveMingle(mingleId)
        }
    }

    suspend fun getMyLogs(
    ): ApiResult<List<MyMingleLogResponse>> {
        return safeRawApiCall {
            mingleApi.getMyLogs()
        }
    }

    suspend fun getMingleLogs(
        mingleId: Int
    ): ApiResult<List<MingleLogResponse>> {
        return safeRawApiCall {
            mingleApi.getMingleLogs(mingleId)
        }
    }

    suspend fun updateMingleImage(
        mingleId: Int,
        bannerImageUrl: File
    ): ApiResult<String> {
        return safeApiCall {
            mingleApi.updateMingleImage(
                mingleId,
                MultipartBody.Part.createFormData(
                    "mingleImage",
                    bannerImageUrl.name,
                    bannerImageUrl.asRequestBody("image/*".toMediaType())
                )
            );
        }
    }
}