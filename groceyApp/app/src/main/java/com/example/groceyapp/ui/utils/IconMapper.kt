package com.example.groceyapp.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps an ImageVector icon to its string representation for backend storage
 */
fun mapIconToString(icon: ImageVector): String {
    return when (icon) {
        Icons.Rounded.ShoppingCart -> "shopping_cart"
        Icons.Filled.Store -> "store"
        Icons.Filled.Inventory2 -> "inventory"
        Icons.AutoMirrored.Filled.List -> "list"
        Icons.Filled.Star -> "star"
        Icons.Outlined.StarBorder -> "star_border"
        Icons.Filled.Add -> "add"
        Icons.Filled.Remove -> "remove"
        Icons.Filled.MoreVert -> "more_vert"
        Icons.Filled.Delete -> "delete"
        Icons.Filled.Edit -> "edit"
        Icons.Filled.Share -> "share"
        Icons.Filled.FilterList -> "filter_list"
        Icons.Filled.Search -> "search"
        Icons.Filled.ChevronRight -> "chevron_right"
        Icons.Filled.Circle -> "circle"
        Icons.Filled.Person -> "person"
        Icons.Filled.Email -> "email"
        Icons.Filled.Lock -> "lock"
        Icons.Filled.DarkMode -> "dark_mode"
        Icons.Filled.Language -> "language"
        Icons.Filled.Visibility -> "visibility"
        Icons.Filled.VisibilityOff -> "visibility_off"
        Icons.AutoMirrored.Filled.ArrowBack -> "arrow_back"
        Icons.Filled.Home -> "home"
        Icons.Filled.Bookmark -> "bookmark"
        Icons.Filled.LocalOffer -> "local_offer"
        Icons.Filled.Tag -> "tag"
        Icons.Rounded.LocalGroceryStore -> "local_grocery_store"
        Icons.Filled.Favorite -> "favorite"
        Icons.Filled.FavoriteBorder -> "favorite_border"
        Icons.Filled.ShoppingBasket -> "shopping_basket"
        Icons.Filled.Restaurant -> "restaurant"
        Icons.Filled.Fastfood -> "fastfood"
        Icons.Filled.LocalDining -> "local_dining"
        Icons.Filled.Category -> "category"
        else -> "shopping_cart" // Default fallback
    }
}

/**
 * Maps a string representation to its corresponding ImageVector icon
 */
fun mapStringToIcon(iconName: String?): ImageVector {
    return when (iconName?.lowercase()) {
        "shopping_cart", "shoppingcart" -> Icons.Rounded.ShoppingCart
        "store" -> Icons.Filled.Store
        "inventory", "inventory2" -> Icons.Filled.Inventory2
        "list" -> Icons.AutoMirrored.Filled.List
        "star" -> Icons.Filled.Star
        "star_border", "starborder" -> Icons.Outlined.StarBorder
        "add" -> Icons.Filled.Add
        "remove" -> Icons.Filled.Remove
        "more_vert", "morevert" -> Icons.Filled.MoreVert
        "delete" -> Icons.Filled.Delete
        "edit" -> Icons.Filled.Edit
        "share" -> Icons.Filled.Share
        "filter_list", "filterlist" -> Icons.Filled.FilterList
        "search" -> Icons.Filled.Search
        "chevron_right", "chevronright" -> Icons.Filled.ChevronRight
        "circle" -> Icons.Filled.Circle
        "person" -> Icons.Filled.Person
        "email" -> Icons.Filled.Email
        "lock" -> Icons.Filled.Lock
        "dark_mode", "darkmode" -> Icons.Filled.DarkMode
        "language" -> Icons.Filled.Language
        "visibility" -> Icons.Filled.Visibility
        "visibility_off", "visibilityoff" -> Icons.Filled.VisibilityOff
        "arrow_back", "arrowback" -> Icons.AutoMirrored.Filled.ArrowBack
        "home" -> Icons.Filled.Home
        "bookmark" -> Icons.Filled.Bookmark
        "local_offer", "localoffer" -> Icons.Filled.LocalOffer
        "tag" -> Icons.Filled.Tag
        "local_grocery_store", "localgrocerystore" -> Icons.Rounded.LocalGroceryStore
        "favorite" -> Icons.Filled.Favorite
        "favorite_border", "favoriteborder" -> Icons.Filled.FavoriteBorder
        "shopping_basket", "shoppingbasket" -> Icons.Filled.ShoppingBasket
        "restaurant" -> Icons.Filled.Restaurant
        "fastfood" -> Icons.Filled.Fastfood
        "local_dining", "localdining" -> Icons.Filled.LocalDining
        "category" -> Icons.Filled.Category
        else -> Icons.Rounded.ShoppingCart // Default fallback
    }
}
