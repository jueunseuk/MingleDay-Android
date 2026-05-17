package returns.mingleday.app.data.repository

import returns.mingleday.app.data.remote.api.CategoryApi
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.app.data.remote.model.category.UpsertCategoryRequest
import returns.mingleday.app.data.remote.network.ApiResult
import returns.mingleday.app.data.remote.network.RetrofitClient
import returns.mingleday.app.data.remote.network.safeApiCall
import returns.mingleday.app.data.remote.network.safeRawApiCall

class CategoryRepository {

    private val categoryApi: CategoryApi = RetrofitClient.createApi(CategoryApi::class.java)

    suspend fun getMingleCategory(
        mingleId: Int
    ): ApiResult<List<CategoryResponse>> {
        return safeRawApiCall {
            categoryApi.getMingleCategories(mingleId)
        }
    }

    suspend fun createMingleCategory(
        mingleId: Int,
        upsertCategoryRequest: UpsertCategoryRequest
    ): ApiResult<String> {
        return safeApiCall {
            categoryApi.createMingleCategory(mingleId, upsertCategoryRequest)
        }
    }

    suspend fun modifyMingleCategory(
        mingleId: Int,
        categoryId: Long,
        upsertCategoryRequest: UpsertCategoryRequest
    ): ApiResult<String> {
        return safeApiCall {
            categoryApi.modifyMingleCategory(mingleId, categoryId, upsertCategoryRequest)
        }
    }

    suspend fun deleteMingleCategory(
        mingleId: Int,
        categoryId: Long
    ): ApiResult<String> {
        return safeApiCall {
            categoryApi.deleteMingleCategory(mingleId, categoryId)
        }
    }
}