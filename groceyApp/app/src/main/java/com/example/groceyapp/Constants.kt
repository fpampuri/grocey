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

    // Default category canonical name and metadata markers
    const val MISCELLANEOUS_CATEGORY_NAME = "Miscellaneous"
    const val MISC_CATEGORY_META_KEY = "builtin"
    const val MISC_CATEGORY_META_VALUE = "misc"
    const val MISC_CATEGORY_ICON_NAME = "category"
}
