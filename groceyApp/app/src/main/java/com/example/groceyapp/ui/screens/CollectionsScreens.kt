package com.example.groceyapp.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.CategoryCard
import com.example.groceyapp.ui.components.CategoryCardData
import com.example.groceyapp.ui.components.general.GenericCollectionScreen
import com.example.groceyapp.ui.components.ListCard
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.ProductItemData
import com.example.groceyapp.ui.components.general.TopSearchBar
import com.example.groceyapp.ui.theme.BrandGreenLight
import com.example.groceyapp.ui.theme.BrandGreenLightDarkTheme

enum class HomeDestination(@StringRes val labelRes: Int, val icon: ImageVector) {
    Pantry(R.string.pantry_tab, Icons.Filled.Inventory2),
    Products(R.string.products_tab, Icons.Filled.Store),
    Lists(R.string.lists_tab, Icons.AutoMirrored.Filled.List)
}

@Composable
fun ListsScreen(
    modifier: Modifier = Modifier,
    items: List<ListCardData>? = null,
    onFilterClick: () -> Unit = {},
    onListClick: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {},
    onRename: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val resolvedItems = items ?: emptyList()

    GenericCollectionScreen(
        items = resolvedItems,
        placeholder = stringResource(R.string.search_lists_placeholder),
        filterDescription = stringResource(R.string.filter_content_description),
        showFilterInside = true,
        onFilterClick = onFilterClick,
        onMenuClick = onMenuClick,
        itemMatchesQuery = { list, q -> list.title.contains(q, ignoreCase = true) },
        itemKey = { it.id },
        itemContent = { item ->
            ListCard(
                data = item,
                onClick = onListClick,
                onFavoriteToggle = onFavoriteToggle,
                onRename = onRename,
                onDelete = onDelete,
                onShare = onShare
            )
        },
        emptyIcon = Icons.AutoMirrored.Filled.List,
        emptyMessageRes = R.string.empty_lists_message,
        emptyHintRes = R.string.empty_lists_hint
    )
}

@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    items: List<CategoryCardData>? = null,
    onFilterClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val resolvedItems = items ?: emptyList()

    GenericCollectionScreen(
        items = resolvedItems,
        placeholder = stringResource(R.string.search_pantry_placeholder),
        filterDescription = stringResource(R.string.filter_content_description),
        showFilterInside = true,
        onFilterClick = onFilterClick,
        onMenuClick = onMenuClick,
        itemMatchesQuery = { category, q ->
            category.title.contains(q, ignoreCase = true) ||
                category.products.any { product -> product.contains(q, ignoreCase = true) }
        },
        itemKey = { it.title },
        itemContent = { item -> CategoryCard(item) },
        emptyIcon = Icons.Filled.Inventory2,
        emptyMessageRes = R.string.empty_pantry_message,
        emptyHintRes = R.string.empty_pantry_hint
    )
}

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    items: List<CategoryCardData>? = null,
    onFilterClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onCategoryDelete: (Long?) -> Unit = {},
    onCategoryRename: (Long?) -> Unit = {}
) {
    val resolvedItems = items ?: emptyList()

    GenericCollectionScreen(
        items = resolvedItems,
        placeholder = stringResource(R.string.search_products_placeholder),
        filterDescription = stringResource(R.string.filter_content_description),
        showFilterInside = true,
        onFilterClick = onFilterClick,
        onMenuClick = onMenuClick,
        itemMatchesQuery = { category, q ->
            category.title.contains(q, ignoreCase = true) ||
                category.products.any { product -> product.contains(q, ignoreCase = true) }
        },
        itemKey = { it.title },
        itemContent = { item -> 
            CategoryCard(
                item,
                onDelete = onCategoryDelete,
                onRename = onCategoryRename
            )
        },
        emptyIcon = Icons.Filled.Store,
        emptyMessageRes = R.string.empty_products_message,
        emptyHintRes = R.string.empty_products_hint
    )
}

@Composable
fun HomeBottomBar(
    currentDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit
) {
    val containerColor = if (isSystemInDarkTheme()) {
        BrandGreenLightDarkTheme
    } else {
        BrandGreenLight
    }

    val selectedColor = Color.White
    val unselectedColor = Color.White.copy(alpha = 0.7f)
    val indicatorColor = Color.White.copy(alpha = 0.2f)

    NavigationBar(containerColor = containerColor) {
        HomeDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == currentDestination,
                onClick = { onDestinationSelected(destination) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(text = stringResource(id = destination.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    indicatorColor = indicatorColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}

@Composable
private fun defaultListItems(): List<ListCardData> {
    return emptyList()
}

@Composable
private fun defaultPantryItems(): List<CategoryCardData> {
    return emptyList()
}

@Composable
private fun defaultProductItems(): List<CategoryCardData> {
    return emptyList()
}