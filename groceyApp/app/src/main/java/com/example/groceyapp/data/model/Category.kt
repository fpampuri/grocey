package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Category data model
 */
data class Category(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Category creation data
 */
data class CategoryCreate(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Category update data
 */
data class CategoryUpdate(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)
