package com.example.groceyapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Shopping list data model
 */
data class ShoppingList(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("recurring")
    val recurring: Boolean? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null,
    
    @SerializedName("owner")
    val owner: User? = null,
    
    @SerializedName("sharedWith")
    val sharedWith: List<User>? = null,
    
    @SerializedName("lastPurchasedAt")
    val lastPurchasedAt: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Shopping list creation data
 */
data class ShoppingListCreate(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("recurring")
    val recurring: Boolean? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Shopping list update data
 */
data class ShoppingListUpdate(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("recurring")
    val recurring: Boolean? = null,
    
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Share list request
 */
data class ShareListRequest(
    @SerializedName("emails")
    val emails: List<String>
)
