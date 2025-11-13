package com.example.groceyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.model.*
import com.example.groceyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Authentication ViewModel
 * Manages user authentication state and operations
 */
class AuthViewModel : ViewModel() {
    
    private val repository = UserRepository()
    
    // Authentication state
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()
    
    // Loading & error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Login with email and password
     */
    fun login(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.login(Credentials(email, password))
            
            when (result) {
                is ApiResult.Success -> {
                    val token = result.data.token
                    _authToken.value = token
                    ApiClient.setAuthToken(token)
                    _isAuthenticated.value = true
                    
                    // Load user profile
                    loadUserProfile()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {
                    // Already handled
                }
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Register new user
     * Note: Verification token is sent via email, not returned in response
     */
    fun register(
        email: String,
        name: String,
        surname: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val registrationData = RegistrationData(
                email = email,
                name = name,
                surname = surname,
                password = password
            )
            
            val result = repository.register(registrationData)
            
            when (result) {
                is ApiResult.Success -> {
                    // Registration successful - verification token sent via email
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Verify account with code
     */
    fun verifyAccount(code: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.verifyAccount(VerificationCode(code))
            
            when (result) {
                is ApiResult.Success -> {
                    val token = result.data.token
                    _authToken.value = token
                    ApiClient.setAuthToken(token)
                    _isAuthenticated.value = true
                    
                    loadUserProfile()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Load current user profile
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            val result = repository.getProfile()
            
            when (result) {
                is ApiResult.Success -> {
                    _currentUser.value = result.data
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    /**
     * Logout current user
     */
    fun logout(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.logout()
            
            // Clear local state
            _isAuthenticated.value = false
            _currentUser.value = null
            _authToken.value = null
            ApiClient.setAuthToken(null)
            
            onSuccess()
        }
    }
    
    /**
     * Update user profile
     */
    fun updateProfile(name: String, surname: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.updateProfile(
                UpdateUserProfile(name = name, surname = surname)
            )
            
            when (result) {
                is ApiResult.Success -> {
                    _currentUser.value = result.data
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Change password
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.changePassword(
                PasswordChange(currentPassword, newPassword)
            )
            
            when (result) {
                is ApiResult.Success -> {
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                }
                is ApiResult.Loading -> {}
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Set authentication token (for app startup with saved token)
     */
    fun setToken(token: String) {
        _authToken.value = token
        ApiClient.setAuthToken(token)
        _isAuthenticated.value = true
        loadUserProfile()
    }
}
