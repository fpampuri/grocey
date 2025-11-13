package com.example.groceyapp.data.repository

import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.api.ApiHelper
import com.example.groceyapp.data.model.*

/**
 * User repository
 * Handles all user-related data operations including authentication
 */
class UserRepository {
    
    private val userApi = ApiClient.userApi
    
    // Authentication
    
    suspend fun register(registrationData: RegistrationData): ApiResult<RegisteredUser> {
        return ApiHelper.safeApiCall {
            userApi.register(registrationData)
        }
    }
    
    suspend fun login(credentials: Credentials): ApiResult<AuthenticationToken> {
        return ApiHelper.safeApiCall {
            userApi.login(credentials)
        }
    }
    
    suspend fun verifyAccount(verificationCode: VerificationCode): ApiResult<AuthenticationToken> {
        return ApiHelper.safeApiCall {
            userApi.verifyAccount(verificationCode)
        }
    }
    
    suspend fun logout(): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            userApi.logout()
        }
    }
    
    // Password recovery
    
    suspend fun forgotPassword(email: String): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            userApi.forgotPassword(email)
        }
    }
    
    suspend fun resetPassword(passwordReset: PasswordReset): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            userApi.resetPassword(passwordReset)
        }
    }
    
    suspend fun resendVerification(email: String): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            userApi.resendVerification(mapOf("email" to email))
        }
    }
    
    // Profile management
    
    suspend fun getProfile(): ApiResult<User> {
        return ApiHelper.safeApiCall {
            userApi.getProfile()
        }
    }
    
    suspend fun updateProfile(profileData: UpdateUserProfile): ApiResult<User> {
        return ApiHelper.safeApiCall {
            userApi.updateProfile(profileData)
        }
    }
    
    suspend fun changePassword(passwordChange: PasswordChange): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            userApi.changePassword(passwordChange)
        }
    }
    
    suspend fun deleteAccount(): ApiResult<Unit> {
        return ApiHelper.safeApiCallUnit {
            userApi.deleteAccount()
        }
    }
}
