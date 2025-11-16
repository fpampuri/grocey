package com.example.groceyapp.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Helper object for managing app locale/language settings
 */
object LocaleHelper {
    
    private const val PREFS_NAME = "app_settings"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_MENU_OPEN = "menu_open_on_recreate"
    private const val KEY_SETTINGS_EXPANDED = "settings_expanded_on_recreate"
    
    /**
     * Set the app's language and recreate activity
     * @param activity Current activity
     * @param languageCode Language code (e.g., "en", "es")
     */
    fun setLocale(activity: Activity, languageCode: String) {
        // Save preference
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_LANGUAGE, languageCode)
            .putBoolean(KEY_MENU_OPEN, true)
            .putBoolean(KEY_SETTINGS_EXPANDED, true)
            .apply()
        
        // Update configuration
        updateConfiguration(activity, languageCode)
        
        // Recreate activity to apply changes
        activity.recreate()
    }
    
    /**
     * Check if menu should be opened after recreation
     */
    fun shouldOpenMenuOnStart(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val shouldOpen = prefs.getBoolean(KEY_MENU_OPEN, false)
        if (shouldOpen) {
            // Clear flag after reading
            prefs.edit().putBoolean(KEY_MENU_OPEN, false).apply()
        }
        return shouldOpen
    }
    
    /**
     * Check if settings section should be expanded after recreation
     */
    fun shouldExpandSettingsOnStart(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val shouldExpand = prefs.getBoolean(KEY_SETTINGS_EXPANDED, false)
        if (shouldExpand) {
            // Clear flag after reading
            prefs.edit().putBoolean(KEY_SETTINGS_EXPANDED, false).apply()
        }
        return shouldExpand
    }
    
    /**
     * Apply saved locale on activity creation
     */
    fun onActivityCreated(activity: Activity) {
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(KEY_LANGUAGE, null)
        
        if (languageCode != null) {
            updateConfiguration(activity, languageCode)
        }
    }
    
    /**
     * Get current app language
     * @return Language code (e.g., "en", "es")
     */
    fun getCurrentLocale(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, null) ?: 
            context.resources.configuration.locales[0].language
    }
    
    private fun updateConfiguration(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
