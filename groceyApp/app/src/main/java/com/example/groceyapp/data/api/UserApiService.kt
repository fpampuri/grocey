package com.example.groceyapp.data.api

import com.example.groceyapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * User API service interface
 * Handles authentication and user profile operations
 */
interface UserApiService {
    
    // Authentication endpoints
    
    @POST("users/register")
    suspend fun register(
        @Body registrationData: RegistrationData
    ): Response<RegisteredUser>
    
    @POST("users/login")
    suspend fun login(
        @Body credentials: Credentials
    ): Response<AuthenticationToken>
    
    @POST("users/verify-account")
    suspend fun verifyAccount(
        @Body verificationCode: VerificationCode
    ): Response<AuthenticationToken>
    
    @POST("users/logout")
    suspend fun logout(): Response<Unit>
    
    // Password recovery
    
    @POST("users/forgot-password")
    suspend fun forgotPassword(
        @Query("email") email: String
    ): Response<Unit>
    
    @POST("users/reset-password")
    suspend fun resetPassword(
        @Body passwordReset: PasswordReset
    ): Response<Unit>
    
    @POST("users/send-verification")
    suspend fun resendVerification(
        @Body email: Map<String, String>  // { "email": "user@example.com" }
    ): Response<Unit>
    
    // Profile management
    
    @GET("users/profile")
    suspend fun getProfile(): Response<User>
    
    @PUT("users/profile")
    suspend fun updateProfile(
        @Body profileData: UpdateUserProfile
    ): Response<User>
    
    @POST("users/change-password")
    suspend fun changePassword(
        @Body passwordChange: PasswordChange
    ): Response<Unit>
    
    @DELETE("users/profile")
    suspend fun deleteAccount(): Response<Unit>
}
