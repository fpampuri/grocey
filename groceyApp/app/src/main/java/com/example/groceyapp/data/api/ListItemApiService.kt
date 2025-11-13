package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.ListItem
import com.example.groceyapp.data.model.ListItemCreate
import com.example.groceyapp.data.model.ListItemUpdate
import retrofit2.Response
import retrofit2.http.*

/**
 * List Item API service interface
 * Manages items within shopping lists
 */
interface ListItemApiService {
    
    @POST("shopping-lists/{listId}/items")
    suspend fun createListItem(
        @Path("listId") listId: Int,
        @Body listItem: ListItemCreate
    ): Response<ListItem>
    
    @GET("shopping-lists/{listId}/items")
    suspend fun getAllListItems(
        @Path("listId") listId: Int
    ): Response<List<ListItem>>
    
    @GET("shopping-lists/{listId}/items/{itemId}")
    suspend fun getListItem(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int
    ): Response<ListItem>
    
    @PUT("shopping-lists/{listId}/items/{itemId}")
    suspend fun updateListItem(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int,
        @Body listItem: ListItemUpdate
    ): Response<ListItem>
    
    @DELETE("shopping-lists/{listId}/items/{itemId}")
    suspend fun deleteListItem(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int
    ): Response<Unit>
    
    // Mark as purchased
    
    @POST("shopping-lists/{listId}/items/{itemId}/purchase")
    suspend fun markAsPurchased(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int
    ): Response<ListItem>
}
