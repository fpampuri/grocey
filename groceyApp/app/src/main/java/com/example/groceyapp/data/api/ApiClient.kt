package com.example.groceyapp.data.api

import android.os.Build
import com.example.groceyapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit API configuration and client builder
 * Provides singleton instances of API services
 */
object ApiClient {
    
    private const val EMULATOR_BASE_URL = "http://10.0.2.2:8080/api/"
    private const val DEVICE_BASE_URL = "http://192.168.1.22:8080/api/"
    
    private const val TIMEOUT_SECONDS = 60L
    
    // Auth token storage
    private var authToken: String? = null
    
    /**
     * Set the authentication token for API requests
     */
    fun setAuthToken(token: String?) {
        authToken = token
    }
    
    /**
     * Get the current authentication token
     */
    fun getAuthToken(): String? = authToken
    
    /**
     * Auth interceptor to add Bearer token to requests
     */
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        authToken?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        chain.proceed(requestBuilder.build())
    }
    
    /**
     * Logging interceptor for debugging network requests
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    /**
     * OkHttp client with interceptors
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    
    /**
     * Memoized base URL so every service shares the same resolved endpoint.
     */
    private val resolvedBaseUrl: String by lazy { determineBaseUrl() }
    
    /**
     * Retrofit instance
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(resolvedBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    /**
     * Create API service instances
     */
    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
    
    val categoryApi: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }
    
    val productApi: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }
    
    val shoppingListApi: ShoppingListApiService by lazy {
        retrofit.create(ShoppingListApiService::class.java)
    }
    
    val listItemApi: ListItemApiService by lazy {
        retrofit.create(ListItemApiService::class.java)
    }
    
    val pantryApi: PantryApiService by lazy {
        retrofit.create(PantryApiService::class.java)
    }
    
    val pantryItemApi: PantryItemApiService by lazy {
        retrofit.create(PantryItemApiService::class.java)
    }
    
    private fun determineBaseUrl(): String {
        val configured = BuildConfig.API_BASE_URL.trim()
        if (configured.isNotEmpty()) {
            return configured.ensureTrailingSlash()
        }
        
        return if (isRunningOnEmulator()) {
            EMULATOR_BASE_URL
        } else {
            DEVICE_BASE_URL
        }
    }
    
    private fun isRunningOnEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val product = Build.PRODUCT.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        return fingerprint.contains("generic") ||
            fingerprint.contains("emulator") ||
            product.contains("sdk") ||
            manufacturer.contains("genymotion")
    }
    
    private fun String.ensureTrailingSlash(): String {
        return if (endsWith("/")) this else "$this/"
    }
}
