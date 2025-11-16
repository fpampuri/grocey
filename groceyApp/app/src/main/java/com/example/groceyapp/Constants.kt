package com.example.groceyapp

/**
 * Application-wide constants
 */
object Constants {
    /**
     * The ID of the Miscellaneous category.
     * This category is the default category for products that don't fit into other categories.
     * It cannot be deleted or renamed.
     */
    const val MISCELLANEOUS_CATEGORY_ID = -1L
    
    /**
     * The name of the Miscellaneous category.
     * This is used to identify the default category across the app.
     */
    const val MISCELLANEOUS_CATEGORY_NAME = "Miscellaneous"
    
    /**
     * Metadata key used to mark protected/system categories.
     */
    const val MISC_CATEGORY_META_KEY = "system_category"
    
    /**
     * Metadata value used to mark protected/system categories.
     */
    const val MISC_CATEGORY_META_VALUE = "true"
    
    /**
     * Default icon name for the Miscellaneous category.
     */
    const val MISC_CATEGORY_ICON_NAME = "category"
}
