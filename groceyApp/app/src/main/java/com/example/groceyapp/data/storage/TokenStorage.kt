package com.example.groceyapp.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Token storage utility using SharedPreferences
 * Handles saving and retrieving authentication tokens
 */
object TokenStorage {
    
    private const val PREFS_NAME = "grocey_app_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_EMAIL = "user_email"
    
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize the storage with application context
     * Call this from Application class or MainActivity onCreate
     */
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }
    
    /**
     * Save authentication token
     */
    fun saveToken(token: String) {
        prefs?.edit()?.apply {
            putString(KEY_AUTH_TOKEN, token)
            apply()
        }
    }
    
    /**
     * Get saved authentication token
     */
    fun getToken(): String? {
        return prefs?.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Save user email (optional, for auto-fill)
     */
    fun saveUserEmail(email: String) {
        prefs?.edit()?.apply {
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }
    
    /**
     * Get saved user email
     */
    fun getUserEmail(): String? {
        return prefs?.getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Clear all stored data (on logout)
     */
    fun clear() {
        prefs?.edit()?.apply {
            clear()
            apply()
        }
    }
    
    /**
     * Check if user has a saved token
     */
    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}
