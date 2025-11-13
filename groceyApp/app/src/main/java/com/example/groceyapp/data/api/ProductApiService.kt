package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.Product
import com.example.groceyapp.data.model.ProductCreate
import com.example.groceyapp.data.model.ProductUpdate
import retrofit2.Response
import retrofit2.http.*

/**
 * Product API service interface
 */
interface ProductApiService {
    
    @POST("products")
    suspend fun createProduct(
        @Body product: ProductCreate
    ): Response<Product>
    
    @GET("products")
    suspend fun getAllProducts(): Response<List<Product>>
    
    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: Int
    ): Response<Product>
    
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductUpdate
    ): Response<Product>
    
    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<Unit>
}
