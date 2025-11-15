package com.example.groceyapp.data.repository

import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.api.ApiHelper
import com.example.groceyapp.data.model.*

/**
 * Shopping list repository
 * Handles all shopping list-related data operations
 */
class ShoppingListRepository {
    
    private val shoppingListApi = ApiClient.shoppingListApi
    private val listItemApi = ApiClient.listItemApi
    
    // Shopping list operations
    
    suspend fun createShoppingList(shoppingList: ShoppingListCreate): ApiResult<ShoppingList> {
        return ApiHelper.safeApiCall {
            shoppingListApi.createShoppingList(shoppingList)
        }
    }
    
    suspend fun getAllShoppingLists(): ApiResult<List<ShoppingList>> {
        return when (val result = ApiHelper.safeApiCall { shoppingListApi.getAllShoppingLists() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.data) // Extract data from pagination
            is ApiResult.Error -> result
            is ApiResult.Loading -> result
        }
    }
    
    suspend fun getShoppingList(id: Int): ApiResult<ShoppingList> {
        return ApiHelper.safeApiCall {
            shoppingListApi.getShoppingList(id)
        }
    }
    
    suspend fun updateShoppingList(id: Int, shoppingList: ShoppingListUpdate): ApiResult<ShoppingList> {
        return ApiHelper.safeApiCall {
            shoppingListApi.updateShoppingList(id, shoppingList)
        }
    }
    
    suspend fun deleteShoppingList(id: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            shoppingListApi.deleteShoppingList(id)
        }
    }
    
    // Sharing operations
    
    suspend fun shareShoppingList(id: Int, emails: List<String>): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            shoppingListApi.shareShoppingList(id, ShareListRequest(emails))
        }
    }
    
    suspend fun unshareShoppingList(id: Int, userId: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            shoppingListApi.unshareShoppingList(id, userId)
        }
    }
    
    // Purchase operation
    
    suspend fun purchaseShoppingList(id: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            shoppingListApi.purchaseShoppingList(id)
        }
    }
    
    // List item operations
    
    suspend fun createListItem(listId: Int, listItem: ListItemCreate): ApiResult<ListItem> {
        return when (val result = ApiHelper.safeApiCall { listItemApi.createListItem(listId, listItem) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.item)
            is ApiResult.Error -> result
            is ApiResult.Loading -> result
        }
    }
    
    suspend fun getAllListItems(listId: Int): ApiResult<List<ListItem>> {
        return when (val result = ApiHelper.safeApiCall { listItemApi.getAllListItems(listId, null, null) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> result
            is ApiResult.Loading -> result
        }
    }

    suspend fun getListItemCount(listId: Int): ApiResult<Int> {
        return when (val result = ApiHelper.safeApiCall { listItemApi.getAllListItems(listId, page = 1, perPage = 1) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.pagination.total)
            is ApiResult.Error -> result.copy()
            is ApiResult.Loading -> result
        }
    }
    
    suspend fun getListItem(listId: Int, itemId: Int): ApiResult<ListItem> {
        return ApiHelper.safeApiCall {
            listItemApi.getListItem(listId, itemId)
        }
    }
    
    suspend fun updateListItem(listId: Int, itemId: Int, listItem: ListItemUpdate): ApiResult<ListItem> {
        return ApiHelper.safeApiCall {
            listItemApi.updateListItem(listId, itemId, listItem)
        }
    }
    
    suspend fun deleteListItem(listId: Int, itemId: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            listItemApi.deleteListItem(listId, itemId)
        }
    }
    
    suspend fun markItemAsPurchased(listId: Int, itemId: Int, purchased: Boolean): ApiResult<ListItem> {
        return ApiHelper.safeApiCall {
            listItemApi.togglePurchased(listId, itemId, com.example.groceyapp.data.api.PurchasedToggle(purchased))
        }
    }
}
