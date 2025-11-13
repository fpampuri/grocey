package com.example.groceyapp

import android.app.Application
import com.example.groceyapp.data.api.ApiClient
import com.example.groceyapp.data.storage.TokenStorage

/**
 * Application class for Grocey App
 * Initializes global components like TokenStorage
 */
class GroceyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize token storage
        TokenStorage.init(applicationContext)
        
        // Restore saved token if exists
        val savedToken = TokenStorage.getToken()
        if (!savedToken.isNullOrEmpty()) {
            ApiClient.setAuthToken(savedToken)
        }
    }
}
