package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.ApiError
import com.example.groceyapp.data.model.ApiResult
import com.google.gson.Gson
import retrofit2.Response

/**
 * Helper object for handling API responses
 * Converts Retrofit Response to ApiResult sealed class
 */
object ApiHelper {
    
    /**
     * Process a Retrofit Response and return an ApiResult
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("Empty response body", response.code())
                }
            } else {
                // Try to parse error body
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    apiError.message
                } catch (e: Exception) {
                    errorBody ?: "Unknown error occurred"
                }
                
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(
                message = e.localizedMessage ?: "Network error occurred",
                code = null
            )
        }
    }
    
    /**
     * Process a Retrofit Response for Unit (void) responses
     */
    suspend fun safeApiCallUnit(apiCall: suspend () -> Response<Unit>): ApiResult<Unit> {
        return try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    apiError.message
                } catch (e: Exception) {
                    errorBody ?: "Unknown error occurred"
                }
                
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(
                message = e.localizedMessage ?: "Network error occurred",
                code = null
            )
        }
    }
}
