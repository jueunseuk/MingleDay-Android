package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.MingleApi
import returns.mingleday.app.data.remote.model.common.SuccessResponse
import returns.mingleday.app.data.remote.model.mingle.CreateMingleRequest
import returns.mingleday.app.data.remote.model.mingle.CreateMingleResponse
import returns.mingleday.app.data.remote.model.mingle.InviteMingleRequest
import returns.mingleday.app.data.remote.model.mingle.MinglePermissionRequest
import returns.mingleday.app.data.remote.model.mingle.MingleResponse
import returns.mingleday.app.data.remote.model.mingle.MinglesResponse
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeApiCall
import returns.mingleday.app.data.remote.network.safeRawApiCall

class MingleRepository {

    private val mingleApi: MingleApi = RetrofitClient.createApi(MingleApi::class.java)

    suspend fun getMyMingles(
    ): ApiResult<List<MinglesResponse>> {
        return safeRawApiCall {
            mingleApi.getMyMingles()
        }
    }

    suspend fun getMingle(
        mingleId: Int
    ): ApiResult<MingleResponse> {
        return safeRawApiCall {
            mingleApi.getMingle(mingleId)
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
}