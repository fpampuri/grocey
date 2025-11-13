package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.Category
import com.example.groceyapp.data.model.CategoryCreate
import com.example.groceyapp.data.model.CategoryUpdate
import com.example.groceyapp.data.model.PaginatedResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Category API service interface
 */
interface CategoryApiService {
    
    @POST("categories")
    suspend fun createCategory(
        @Body category: CategoryCreate
    ): Response<Category>
    
    @GET("categories")
    suspend fun getAllCategories(): Response<PaginatedResponse<Category>>
    
    @GET("categories/{id}")
    suspend fun getCategory(
        @Path("id") id: Int
    ): Response<Category>
    
    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body category: CategoryUpdate
    ): Response<Category>
    
    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    ): Response<Unit>
}
