package com.example.groceyapp.ui.lists

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
import com.example.groceyapp.ui.components.GenericCollectionScreen
import com.example.groceyapp.ui.components.ListCard
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.ProductItemData
import com.example.groceyapp.ui.components.TopSearchBar
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
    val resolvedItems = items ?: defaultListItems()
    // API CHANGE: Replace `defaultListItems()` with data provided by a ViewModel/Repository
    // (e.g. `val lists by collectionsViewModel.listsState.collectAsState()`),
    // and remove the hard-coded mock data usage here.

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
        }
    )
}

@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    items: List<CategoryCardData>? = null,
    onFilterClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val resolvedItems = items ?: defaultPantryItems()
    // API CHANGE: Replace `defaultPantryItems()` with data from a ViewModel/Repository
    // (e.g. `val pantry by collectionsViewModel.pantryState.collectAsState()`),
    // and perform filtering through the ViewModel or repository APIs.

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
        itemContent = { item -> CategoryCard(item) }
    )
}

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    items: List<CategoryCardData>? = null,
    onFilterClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val resolvedItems = items ?: defaultProductItems()
    // API CHANGE: Replace `defaultProductItems()` with data from a ViewModel/Repository
    // (e.g. `val products by collectionsViewModel.productsState.collectAsState()`),
    // and perform filtering through the ViewModel or repository APIs.

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
        itemContent = { item -> CategoryCard(item) }
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

// Public version for external access (e.g., MainActivity)
@Composable
fun defaultListItemsPublic(): List<ListCardData> {
    return defaultListItems()
}

@Composable
// API CHANGE: `defaultListItems`, `defaultPantryItems`, and `defaultProductItems` are
// mock/sample data used for previews and local testing. When integrating an API/DB,
// move these samples to a preview/test-only file (e.g. `ui/preview/SampleData.kt`) and
// fetch real data via a Repository/ViewModel instead of calling these functions.
private fun defaultListItems(): List<ListCardData> {
    return listOf(
        ListCardData(
            id = "list_1",
            title = "Weekly Shopping",
            itemCount = 5,
            leadingIcon = Icons.Rounded.ShoppingCart,
            isFavorite = false,
            isShared = false,
            products = listOf(
                ProductItemData(
                    id = "p1",
                    name = "Milk",
                    category = "Dairy",
                    quantity = 2,
                    isBought = false
                ),
                ProductItemData(
                    id = "p2",
                    name = "Bread",
                    category = "Bakery",
                    quantity = 1,
                    isBought = true
                ),
                ProductItemData(
                    id = "p3",
                    name = "Apples",
                    category = "Fruits",
                    quantity = 6,
                    isBought = false
                ),
                ProductItemData(
                    id = "p4",
                    name = "Chicken Breast",
                    category = "Meat",
                    quantity = 1,
                    isBought = false
                ),
                ProductItemData(
                    id = "p5",
                    name = "Rice",
                    category = "Grains",
                    quantity = 2,
                    isBought = false
                )
            )
        ),
        ListCardData(
            id = "list_2",
            title = "Market List",
            itemCount = 3,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            isFavorite = true,
            isShared = true,
            products = listOf(
                ProductItemData(
                    id = "p6",
                    name = "Tomatoes",
                    category = "Vegetables",
                    quantity = 4,
                    isBought = false
                ),
                ProductItemData(
                    id = "p7",
                    name = "Cheese",
                    category = "Dairy",
                    quantity = 1,
                    isBought = false
                ),
                ProductItemData(
                    id = "p8",
                    name = "Olive Oil",
                    category = "Condiments",
                    quantity = 1,
                    isBought = true
                )
            )
        ),
        ListCardData(
            id = "list_3",
            title = "Cleaning Products",
            itemCount = 0,
            leadingIcon = Icons.Filled.Home,
            isFavorite = false,
            isShared = false,
            products = emptyList()
        )
    )
}

@Composable
private fun defaultPantryItems(): List<CategoryCardData> {
    val countFour = stringResource(id = R.string.items_count, 4)
    val countTwo = stringResource(id = R.string.items_count, 2)
    val countOne = stringResource(id = R.string.items_count, 1)
    return listOf(
        CategoryCardData(
            title = "Rice",
            subtitle = countFour,
            leadingIcon = Icons.Filled.Inventory2,
            badgeText = null,
            products = listOf("Basmati Rice", "Brown Rice", "Jasmine Rice", "Wild Rice")
        ),
        CategoryCardData(
            title = "Olive Oil",
            subtitle = countTwo,
            leadingIcon = Icons.Filled.Inventory2,
            products = listOf("Extra Virgin", "Light Olive Oil")
        ),
        CategoryCardData(
            title = "Paprika",
            subtitle = countOne,
            leadingIcon = Icons.Filled.Inventory2,
            products = listOf("Smoked Paprika")
        )
    )
}

@Composable
private fun defaultProductItems(): List<CategoryCardData> {
    val countTwelve = stringResource(id = R.string.items_count, 12)
    val countSix = stringResource(id = R.string.items_count, 6)
    val countThree = stringResource(id = R.string.items_count, 3)
    val countTwo = stringResource(id = R.string.items_count, 2)
    return listOf(
        CategoryCardData(
            title = "General",
            subtitle = countTwelve,
            leadingIcon = Icons.Filled.Store,
            products = listOf("Bread", "Eggs", "Butter", "Salt", "Sugar", "Flour")
        ),
        CategoryCardData(
            title = "Dairy",
            subtitle = countTwo,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            products = listOf("Milk", "Cheese")
        ),
        CategoryCardData(
            title = "Bakery",
            subtitle = countThree,
            leadingIcon = Icons.Filled.Store,
            products = listOf("Croissants", "Baguette", "Muffins")
        ),
        CategoryCardData(
            title = "Fruits",
            subtitle = countSix,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            badgeText = stringResource(id = R.string.shared_label),
            products = listOf("Apples", "Bananas", "Oranges", "Grapes", "Strawberries", "Pears")
        ),
        CategoryCardData(
            title = "Vegetables",
            subtitle = countSix,
            leadingIcon = Icons.Filled.Store,
            products = listOf("Carrots", "Tomatoes", "Lettuce", "Onions", "Peppers", "Cucumbers")
        )
    )
}