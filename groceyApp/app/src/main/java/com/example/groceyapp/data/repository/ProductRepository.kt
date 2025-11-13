package com.example.groceyapp.data.repository

import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.api.ApiHelper
import com.example.groceyapp.data.model.*

/**
 * Product repository
 * Handles all product-related data operations
 */
class ProductRepository {
    
    private val productApi = ApiClient.productApi
    
    suspend fun createProduct(product: ProductCreate): ApiResult<Product> {
        return ApiHelper.safeApiCall {
            productApi.createProduct(product)
        }
    }
    
    suspend fun getAllProducts(): ApiResult<List<Product>> {
        return when (val result = ApiHelper.safeApiCall { productApi.getAllProducts() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> result
            is ApiResult.Loading -> result
        }
    }
    
    suspend fun getProduct(id: Int): ApiResult<Product> {
        return ApiHelper.safeApiCall {
            productApi.getProduct(id)
        }
    }
    
    suspend fun updateProduct(id: Int, product: ProductUpdate): ApiResult<Product> {
        return ApiHelper.safeApiCall {
            productApi.updateProduct(id, product)
        }
    }
    
    suspend fun deleteProduct(id: Int): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            productApi.deleteProduct(id)
        }
    }
}
