package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Product data model
 */
data class Product(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("category")
    val category: Category? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Product creation data
 */
data class ProductCreate(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("category")
    val category: CategoryReference? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Product update data
 */
data class ProductUpdate(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("category")
    val category: CategoryReference? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Category reference for product creation/update
 */
data class CategoryReference(
    @SerializedName("id")
    val id: Int
)
