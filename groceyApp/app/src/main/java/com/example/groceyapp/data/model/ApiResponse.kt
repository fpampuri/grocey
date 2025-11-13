package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * API error response
 */
data class ApiError(
    @SerializedName("code")
    val code: Int? = null,
    
    @SerializedName("message")
    val message: String
)

/**
 * Generic API response wrapper for handling success/error states
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
