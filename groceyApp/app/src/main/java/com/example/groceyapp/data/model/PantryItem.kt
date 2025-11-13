package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Pantry item data model (item within a pantry)
 */
data class PantryItem(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("quantity")
    val quantity: Double,
    
    @SerializedName("unit")
    val unit: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("product")
    val product: Product,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Pantry item creation data
 */
data class PantryItemCreate(
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
 * Pantry item update data
 */
data class PantryItemUpdate(
    @SerializedName("quantity")
    val quantity: Double? = null,
    
    @SerializedName("unit")
    val unit: String? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)
