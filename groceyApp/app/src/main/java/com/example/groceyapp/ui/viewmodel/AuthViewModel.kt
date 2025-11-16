package com.example.groceyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.model.*
import com.example.groceyapp.data.repository.UserRepository
import com.example.groceyapp.data.storage.TokenStorage
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
    
    // Pending verification email - exposed as StateFlow so UI can access it
    private val _pendingVerificationEmail = MutableStateFlow<String?>(TokenStorage.getUserEmail())
    val pendingVerificationEmail: StateFlow<String?> = _pendingVerificationEmail.asStateFlow()
    
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
    
    private val _passwordChangeState = MutableStateFlow<PasswordChangeState>(PasswordChangeState.Idle)
    val passwordChangeState: StateFlow<PasswordChangeState> = _passwordChangeState.asStateFlow()
    
    /**
     * Login with email and password
     */
    fun login(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val trimmedEmail = email.trim()
            _pendingVerificationEmail.value = trimmedEmail
            
            val credentials = Credentials(
                email = trimmedEmail,
                password = password
            )
            
            val result = repository.login(credentials)
            
            when (result) {
                is ApiResult.Success -> {
                    val token = result.data.token
                    if (token.isNullOrBlank()) {
                        _errorMessage.value = "Invalid token received from server"
                        _isLoading.value = false
                        return@launch
                    }
                    
                    _authToken.value = token
                    ApiClient.setAuthToken(token)
                    TokenStorage.saveToken(token)
                    _pendingVerificationEmail.value = null
                    
                    // Load profile first, then set authenticated
                    val profileResult = repository.getProfile()
                    when (profileResult) {
                        is ApiResult.Success -> {
                            _currentUser.value = profileResult.data
                            _isAuthenticated.value = true
                            onSuccess()
                        }
                        is ApiResult.Error -> {
                            _errorMessage.value = profileResult.message
                        }
                        is ApiResult.Loading -> {}
                    }
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
            
            val trimmedEmail = email.trim()
            _pendingVerificationEmail.value = trimmedEmail
            TokenStorage.saveUserEmail(trimmedEmail)
            
            val registrationData = RegistrationData(
                email = trimmedEmail,
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
     * Note: Verification does not log the user in - they must login after verification
     */
    fun verifyAccount(code: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.verifyAccount(VerificationCode(code))
            
            when (result) {
                is ApiResult.Success -> {
                    // Verification successful - account is now verified
                    // Clear pending email and show success
                    _pendingVerificationEmail.value = null
                    _errorMessage.value = "Account verified! Please log in."
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
     * Resend verification code to user's email
     */
    fun resendVerification(email: String? = null, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            // Try to get email from: 1) explicit parameter, 2) StateFlow, 3) TokenStorage
            val targetEmail = email?.trim()?.takeIf { it.isNotBlank() }
                ?: _pendingVerificationEmail.value?.takeIf { it.isNotBlank() }
                ?: TokenStorage.getUserEmail()?.takeIf { it.isNotBlank() }
            
            if (targetEmail == null || targetEmail.isBlank()) {
                _errorMessage.value = "Email required. Please register again."
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.resendVerification(targetEmail)

            when (result) {
                is ApiResult.Success -> {
                    _errorMessage.value = "Verification code sent to $targetEmail"
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
            TokenStorage.clear()
            
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
            _passwordChangeState.value = PasswordChangeState.Loading
        
            val result = repository.changePassword(
                PasswordChange(currentPassword, newPassword)
            )

            when (result) {
                is ApiResult.Success -> {
                    _passwordChangeState.value = PasswordChangeState.Success
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _passwordChangeState.value = PasswordChangeState.Error(result.message)
                }
                is ApiResult.Loading -> {
                    _passwordChangeState.value = PasswordChangeState.Loading
                }
            }
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
        TokenStorage.saveToken(token)
        _isAuthenticated.value = true
        loadUserProfile()
    }
    
    /**
     * Try to restore session from saved token
     */
    fun restoreSession() {
        val savedToken = TokenStorage.getToken()
        if (!savedToken.isNullOrEmpty()) {
            setToken(savedToken)
        }
    }
    
    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeState.Idle
    }
}

sealed class PasswordChangeState {
    object Idle : PasswordChangeState()
    object Loading : PasswordChangeState()
    object Success : PasswordChangeState()
    data class Error(val message: String) : PasswordChangeState()
}
