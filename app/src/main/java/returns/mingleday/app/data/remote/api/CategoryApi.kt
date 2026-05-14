package returns.mingleday.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import returns.mingleday.app.data.remote.model.category.CategoryResponse
import returns.mingleday.app.data.remote.model.category.UpsertCategoryRequest
import returns.mingleday.app.data.remote.model.common.SuccessResponse

interface CategoryApi {
    @GET("mingles/{mingleId}/categories/all")
    suspend fun getMingleCategories(
        @Path("mingleId") mingleId: Int
    ): Response<List<CategoryResponse>>

    @POST("mingles/{mingleId}/categories")
    suspend fun createMingleCategory(
        @Path("mingleId") mingleId: Int,
        @Body upsertCategoryRequest: UpsertCategoryRequest
    ): Response<SuccessResponse<String>>

    @PUT("mingles/{mingleId}/categories/{categoryId}")
    suspend fun modifyMingleCategory(
        @Path("mingleId") mingleId: Int,
        @Path("categoryId") categoryId: Long,
        @Body upsertCategoryRequest: UpsertCategoryRequest
    ): Response<SuccessResponse<String>>

    @DELETE("mingles/{mingleId}/categories/{categoryId}")
    suspend fun deleteMingleCategory(
        @Path("mingleId") mingleId: Int,
        @Path("categoryId") categoryId: Long,
    ): Response<SuccessResponse<String>>
}