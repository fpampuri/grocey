package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.Pantry
import com.example.groceyapp.data.model.PantryCreate
import com.example.groceyapp.data.model.PantryUpdate
import com.example.groceyapp.data.model.SharePantryRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Pantry API service interface
 */
interface PantryApiService {
    
    @POST("pantries")
    suspend fun createPantry(
        @Body pantry: PantryCreate
    ): Response<Pantry>
    
    @GET("pantries")
    suspend fun getAllPantries(): Response<List<Pantry>>
    
    @GET("pantries/{id}")
    suspend fun getPantry(
        @Path("id") id: Int
    ): Response<Pantry>
    
    @PUT("pantries/{id}")
    suspend fun updatePantry(
        @Path("id") id: Int,
        @Body pantry: PantryUpdate
    ): Response<Pantry>
    
    @DELETE("pantries/{id}")
    suspend fun deletePantry(
        @Path("id") id: Int
    ): Response<Unit>
    
    // Sharing operations
    
    @POST("pantries/{id}/share")
    suspend fun sharePantry(
        @Path("id") id: Int,
        @Body shareRequest: SharePantryRequest
    ): Response<Unit>
    
    @DELETE("pantries/{id}/share/{userId}")
    suspend fun unsharePantry(
        @Path("id") id: Int,
        @Path("userId") userId: Int
    ): Response<Unit>
}
