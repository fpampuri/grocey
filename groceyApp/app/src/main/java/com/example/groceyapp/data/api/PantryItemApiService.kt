package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.PantryItem
import com.example.groceyapp.data.model.PantryItemCreate
import com.example.groceyapp.data.model.PantryItemUpdate
import retrofit2.Response
import retrofit2.http.*

/**
 * Pantry Item API service interface
 * Manages items within pantries
 */
interface PantryItemApiService {
    
    @POST("pantries/{pantryId}/items")
    suspend fun createPantryItem(
        @Path("pantryId") pantryId: Int,
        @Body pantryItem: PantryItemCreate
    ): Response<PantryItem>
    
    @GET("pantries/{pantryId}/items")
    suspend fun getAllPantryItems(
        @Path("pantryId") pantryId: Int
    ): Response<List<PantryItem>>
    
    @GET("pantries/{pantryId}/items/{itemId}")
    suspend fun getPantryItem(
        @Path("pantryId") pantryId: Int,
        @Path("itemId") itemId: Int
    ): Response<PantryItem>
    
    @PUT("pantries/{pantryId}/items/{itemId}")
    suspend fun updatePantryItem(
        @Path("pantryId") pantryId: Int,
        @Path("itemId") itemId: Int,
        @Body pantryItem: PantryItemUpdate
    ): Response<PantryItem>
    
    @DELETE("pantries/{pantryId}/items/{itemId}")
    suspend fun deletePantryItem(
        @Path("pantryId") pantryId: Int,
        @Path("itemId") itemId: Int
    ): Response<Unit>
}
