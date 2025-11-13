package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * List item data model (item within a shopping list)
 */
data class ListItem(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("quantity")
    val quantity: Double,
    
    @SerializedName("unit")
    val unit: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("purchased")
    val purchased: Boolean? = null,
    
    @SerializedName("product")
    val product: Product,
    
    @SerializedName("lastPurchasedAt")
    val lastPurchasedAt: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * List item creation data
 */
data class ListItemCreate(
    @SerializedName("product")
    val product: ProductReference,
    
    @SerializedName("quantity")
    val quantity: Double = 1.0,
    
    @SerializedName("unit")
    val unit: String = "kg",
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * List item update data
 */
data class ListItemUpdate(
    @SerializedName("quantity")
    val quantity: Double? = null,
    
    @SerializedName("unit")
    val unit: String? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Product reference for list item creation
 */
data class ProductReference(
    @SerializedName("id")
    val id: Int
)
