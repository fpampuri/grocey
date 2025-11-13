package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Pantry data model
 */
data class Pantry(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("owner")
    val owner: User? = null,
    
    @SerializedName("sharedWith")
    val sharedWith: List<User>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Pantry creation data
 */
data class PantryCreate(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Pantry update data
 */
data class PantryUpdate(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Share pantry request
 */
data class SharePantryRequest(
    @SerializedName("emails")
    val emails: List<String>
)
