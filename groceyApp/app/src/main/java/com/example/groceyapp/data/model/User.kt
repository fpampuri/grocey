package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * User data model matching the API response
 */
data class User(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("surname")
    val surname: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * User credentials for login
 */
data class Credentials(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * User registration data
 */
data class RegistrationData(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("surname")
    val surname: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Registration response - backend returns just the user object
 * Verification token is sent via email, not in the response
 */
data class RegisteredUser(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("surname")
    val surname: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Authentication token response
 */
data class AuthenticationToken(
    @SerializedName("token")
    val token: String
)

/**
 * Verification code for account verification
 */
data class VerificationCode(
    @SerializedName("code")
    val code: String
)

/**
 * Password recovery request
 */
data class PasswordRecovery(
    @SerializedName("email")
    val email: String
)

/**
 * Password reset with code and new password
 */
data class PasswordReset(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Update user profile data
 */
data class UpdateUserProfile(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("surname")
    val surname: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Password change request
 */
data class PasswordChange(
    @SerializedName("currentPassword")
    val currentPassword: String,
    
    @SerializedName("newPassword")
    val newPassword: String
)
