package com.example.groceyapp.data.repository

import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.api.ApiHelper
import com.example.groceyapp.data.model.*

/**
 * Pantry repository
 * Handles all pantry-related data operations
 */
class PantryRepository {
    
    private val pantryApi = ApiClient.pantryApi
    private val pantryItemApi = ApiClient.pantryItemApi
    
    // Pantry operations
    
    suspend fun createPantry(pantry: PantryCreate): ApiResult<Pantry> {
        return ApiHelper.safeApiCall {
            pantryApi.createPantry(pantry)
        }
    }
    
    suspend fun getAllPantries(): ApiResult<List<Pantry>> {
        return ApiHelper.safeApiCall {
            pantryApi.getAllPantries()
        }
    }
    
    suspend fun getPantry(id: Int): ApiResult<Pantry> {
        return ApiHelper.safeApiCall {
            pantryApi.getPantry(id)
        }
    }
    
    suspend fun updatePantry(id: Int, pantry: PantryUpdate): ApiResult<Pantry> {
        return ApiHelper.safeApiCall {
            pantryApi.updatePantry(id, pantry)
        }
    }
    
    suspend fun deletePantry(id: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            pantryApi.deletePantry(id)
        }
    }
    
    // Sharing operations
    
    suspend fun sharePantry(id: Int, emails: List<String>): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            pantryApi.sharePantry(id, SharePantryRequest(emails))
        }
    }
    
    suspend fun unsharePantry(id: Int, userId: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            pantryApi.unsharePantry(id, userId)
        }
    }
    
    // Pantry item operations
    
    suspend fun createPantryItem(pantryId: Int, pantryItem: PantryItemCreate): ApiResult<PantryItem> {
        return ApiHelper.safeApiCall {
            pantryItemApi.createPantryItem(pantryId, pantryItem)
        }
    }
    
    suspend fun getAllPantryItems(pantryId: Int): ApiResult<List<PantryItem>> {
        return ApiHelper.safeApiCall {
            pantryItemApi.getAllPantryItems(pantryId)
        }
    }
    
    suspend fun getPantryItem(pantryId: Int, itemId: Int): ApiResult<PantryItem> {
        return ApiHelper.safeApiCall {
            pantryItemApi.getPantryItem(pantryId, itemId)
        }
    }
    
    suspend fun updatePantryItem(pantryId: Int, itemId: Int, pantryItem: PantryItemUpdate): ApiResult<PantryItem> {
        return ApiHelper.safeApiCall {
            pantryItemApi.updatePantryItem(pantryId, itemId, pantryItem)
        }
    }
    
    suspend fun deletePantryItem(pantryId: Int, itemId: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            pantryItemApi.deletePantryItem(pantryId, itemId)
        }
    }
}
