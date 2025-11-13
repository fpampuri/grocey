package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.ShareListRequest
import com.example.groceyapp.data.model.ShoppingList
import com.example.groceyapp.data.model.ShoppingListCreate
import com.example.groceyapp.data.model.ShoppingListUpdate
import retrofit2.Response
import retrofit2.http.*

/**
 * Shopping List API service interface
 */
interface ShoppingListApiService {
    
    @POST("shopping-lists")
    suspend fun createShoppingList(
        @Body shoppingList: ShoppingListCreate
    ): Response<ShoppingList>
    
    @GET("shopping-lists")
    suspend fun getAllShoppingLists(): Response<List<ShoppingList>>
    
    @GET("shopping-lists/{id}")
    suspend fun getShoppingList(
        @Path("id") id: Int
    ): Response<ShoppingList>
    
    @PUT("shopping-lists/{id}")
    suspend fun updateShoppingList(
        @Path("id") id: Int,
        @Body shoppingList: ShoppingListUpdate
    ): Response<ShoppingList>
    
    @DELETE("shopping-lists/{id}")
    suspend fun deleteShoppingList(
        @Path("id") id: Int
    ): Response<Unit>
    
    // Sharing operations
    
    @POST("shopping-lists/{id}/share")
    suspend fun shareShoppingList(
        @Path("id") id: Int,
        @Body shareRequest: ShareListRequest
    ): Response<Unit>
    
    @DELETE("shopping-lists/{id}/share/{userId}")
    suspend fun unshareShoppingList(
        @Path("id") id: Int,
        @Path("userId") userId: Int
    ): Response<Unit>
    
    // Purchase operation
    
    @POST("shopping-lists/{id}/purchase")
    suspend fun purchaseShoppingList(
        @Path("id") id: Int
    ): Response<Unit>
}
