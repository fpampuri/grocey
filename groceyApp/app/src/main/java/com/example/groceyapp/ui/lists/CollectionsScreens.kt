package com.example.groceyapp.ui.lists

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.ui.theme.BrandGreenLight
import com.example.groceyapp.ui.theme.BrandGreenLightDarkTheme

enum class HomeDestination(@StringRes val labelRes: Int, val icon: ImageVector) {
    Pantry(R.string.pantry_tab, Icons.Filled.Inventory2),
    Products(R.string.products_tab, Icons.Filled.Store),
    Lists(R.string.lists_tab, Icons.Filled.List)
}

data class CollectionCardData(
    val title: String,
    val subtitle: String,
    val leadingIcon: ImageVector,
    val badgeText: String? = null,
    val trailingIcon: ImageVector? = null,
    val products: List<String> = emptyList() // List of products in this category
)

@Composable
fun ListsScreen(
    modifier: Modifier = Modifier,
    items: List<CollectionCardData>? = null,
    onFilterClick: () -> Unit = {}
) {
    val resolvedItems = items ?: defaultListItems()
    CollectionContent(
        modifier = modifier,
        searchPlaceholder = stringResource(R.string.search_lists_placeholder),
        filterDescription = stringResource(R.string.filter_content_description),
        items = resolvedItems,
        onFilterClick = onFilterClick
    )
}

@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    items: List<CollectionCardData>? = null,
    onFilterClick: () -> Unit = {}
) {
    val resolvedItems = items ?: defaultPantryItems()
    CollectionContent(
        modifier = modifier,
        searchPlaceholder = stringResource(R.string.search_pantry_placeholder),
        filterDescription = stringResource(R.string.filter_content_description),
        items = resolvedItems,
        onFilterClick = onFilterClick
    )
}

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    items: List<CollectionCardData>? = null,
    onFilterClick: () -> Unit = {}
) {
    val resolvedItems = items ?: defaultProductItems()
    CollectionContent(
        modifier = modifier,
        searchPlaceholder = stringResource(R.string.search_products_placeholder),
        filterDescription = stringResource(R.string.filter_content_description),
        items = resolvedItems,
        onFilterClick = onFilterClick
    )
}

@Composable
private fun CollectionContent(
    modifier: Modifier = Modifier,
    searchPlaceholder: String,
    filterDescription: String,
    items: List<CollectionCardData>,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        SearchRow(
            placeholder = searchPlaceholder,
            filterDescription = filterDescription,
            onFilterClick = onFilterClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(items) { item ->
                CollectionCard(item)
            }
        }
    }
}

@Composable
private fun SearchRow(
    placeholder: String,
    filterDescription: String,
    onFilterClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = filterDescription,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CollectionCard(data: CollectionCardData) {
    var isExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "chevron rotation"
    )

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SolidColor(borderColor)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Main card row (clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = data.leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = data.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                data.badgeText?.let { badge ->
                    SharedBadge(badge)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Chevron icon (rotates when expanded)
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.rotate(rotationAngle)
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Three-dot menu
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = {
                            showMenu = false
                            // TODO: Handle rename
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            // TODO: Handle delete
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Expanded content with products list
            if (isExpanded && data.products.isNotEmpty()) {
                val lightGreenBg = if (isSystemInDarkTheme()) {
                    BrandGreenLightDarkTheme.copy(alpha = 0.15f)
                } else {
                    BrandGreenLight.copy(alpha = 0.15f)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGreenBg)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                ) {
                    data.products.forEach { product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = product,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedBadge(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
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
private fun defaultListItems(): List<CollectionCardData> {
    val countZero = stringResource(id = R.string.items_count, 0)
    val shared = stringResource(id = R.string.shared_label)
    return listOf(
        CollectionCardData(
            title = "Weekly Shopping",
            subtitle = countZero,
            leadingIcon = Icons.Rounded.ShoppingCart,
            products = emptyList()
        ),
        CollectionCardData(
            title = "Market List",
            subtitle = countZero,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            badgeText = shared,
            products = emptyList()
        ),
        CollectionCardData(
            title = "Cleaning Products",
            subtitle = countZero,
            leadingIcon = Icons.Filled.Home,
            products = emptyList()
        )
    )
}

@Composable
private fun defaultPantryItems(): List<CollectionCardData> {
    val countFour = stringResource(id = R.string.items_count, 4)
    val countTwo = stringResource(id = R.string.items_count, 2)
    val countOne = stringResource(id = R.string.items_count, 1)
    return listOf(
        CollectionCardData(
            title = "Rice",
            subtitle = countFour,
            leadingIcon = Icons.Filled.Inventory2,
            badgeText = null,
            products = listOf("Basmati Rice", "Brown Rice", "Jasmine Rice", "Wild Rice")
        ),
        CollectionCardData(
            title = "Olive Oil",
            subtitle = countTwo,
            leadingIcon = Icons.Filled.Inventory2,
            products = listOf("Extra Virgin", "Light Olive Oil")
        ),
        CollectionCardData(
            title = "Paprika",
            subtitle = countOne,
            leadingIcon = Icons.Filled.Inventory2,
            products = listOf("Smoked Paprika")
        )
    )
}

@Composable
private fun defaultProductItems(): List<CollectionCardData> {
    val countTwelve = stringResource(id = R.string.items_count, 12)
    val countSix = stringResource(id = R.string.items_count, 6)
    val countThree = stringResource(id = R.string.items_count, 3)
    val countTwo = stringResource(id = R.string.items_count, 2)
    return listOf(
        CollectionCardData(
            title = "General",
            subtitle = countTwelve,
            leadingIcon = Icons.Filled.Store,
            products = listOf("Bread", "Eggs", "Butter", "Salt", "Sugar", "Flour")
        ),
        CollectionCardData(
            title = "Dairy",
            subtitle = countTwo,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            products = listOf("Milk", "Cheese")
        ),
        CollectionCardData(
            title = "Bakery",
            subtitle = countThree,
            leadingIcon = Icons.Filled.Store,
            products = listOf("Croissants", "Baguette", "Muffins")
        ),
        CollectionCardData(
            title = "Fruits",
            subtitle = countSix,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            badgeText = stringResource(id = R.string.shared_label),
            products = listOf("Apples", "Bananas", "Oranges", "Grapes", "Strawberries", "Pears")
        ),
        CollectionCardData(
            title = "Vegetables",
            subtitle = countSix,
            leadingIcon = Icons.Filled.Store,
            products = listOf("Carrots", "Tomatoes", "Lettuce", "Onions", "Peppers", "Cucumbers")
        )
    )
}