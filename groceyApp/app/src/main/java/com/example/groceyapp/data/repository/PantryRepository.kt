package com.example.groceyapp.data.repository

import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.api.ApiHelper
import com.example.groceyapp.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        return try {
            val response = pantryApi.getAllPantries()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Parse the paginated response manually
                    val gson = Gson()
                    val json = gson.toJson(body)
                    val typeToken = object : TypeToken<PaginatedResponse<Pantry>>() {}.type
                    val paginatedResponse: PaginatedResponse<Pantry> = gson.fromJson(json, typeToken)
                    ApiResult.Success(paginatedResponse.data)
                } else {
                    ApiResult.Error("Empty response body")
                }
            } else {
                ApiResult.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
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
    
    suspend fun getAllPantryItems(
        pantryId: Int,
        page: Int? = null,
        perPage: Int? = null
    ): ApiResult<List<PantryItem>> {
        return try {
            val response = pantryItemApi.getAllPantryItems(pantryId, page, perPage)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val paginatedResponse = parsePantryItems(body)
                    ApiResult.Success(paginatedResponse.data)
                } else {
                    ApiResult.Error("Empty response body")
                }
            } else {
                ApiResult.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getPantryItemCount(pantryId: Int): ApiResult<Int> {
        return try {
            val response = pantryItemApi.getAllPantryItems(pantryId, page = 1, perPage = 1)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val paginatedResponse = parsePantryItems(body)
                    ApiResult.Success(paginatedResponse.pagination.total)
                } else {
                    ApiResult.Error("Empty response body")
                }
            } else {
                ApiResult.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
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

    private fun parsePantryItems(body: Any): PaginatedResponse<PantryItem> {
        val gson = Gson()
        val json = gson.toJson(body)
        val typeToken = object : TypeToken<PaginatedResponse<PantryItem>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}
