package com.example.groceyapp.data.repository

import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.api.ApiHelper
import com.example.groceyapp.data.model.*

/**
 * Category repository
 * Handles all category-related data operations
 */
class CategoryRepository {
    
    private val categoryApi = ApiClient.categoryApi
    
    suspend fun createCategory(category: CategoryCreate): ApiResult<Category> {
        return ApiHelper.safeApiCall {
            categoryApi.createCategory(category)
        }
    }
    
    suspend fun getAllCategories(): ApiResult<List<Category>> {
        return when (val result = ApiHelper.safeApiCall { categoryApi.getAllCategories() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> result
            is ApiResult.Loading -> result
        }
    }
    
    suspend fun getCategory(id: Int): ApiResult<Category> {
        return ApiHelper.safeApiCall {
            categoryApi.getCategory(id)
        }
    }
    
    suspend fun updateCategory(id: Int, category: CategoryUpdate): ApiResult<Category> {
        return ApiHelper.safeApiCall {
            categoryApi.updateCategory(id, category)
        }
    }
    
    suspend fun deleteCategory(id: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            categoryApi.deleteCategory(id)
        }
    }
}
