package com.example.groceyapp.ui.lists

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.CardDefaults
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
    val trailingIcon: ImageVector? = null
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
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SolidColor(borderColor)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                Spacer(modifier = Modifier.width(12.dp))
            }
            data.trailingIcon?.let { trailing ->
                Icon(
                    imageVector = trailing,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
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
            trailingIcon = Icons.Filled.Person
        ),
        CollectionCardData(
            title = "Market List",
            subtitle = countZero,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            badgeText = shared,
            trailingIcon = Icons.Filled.Person
        ),
        CollectionCardData(
            title = "Cleaning Products",
            subtitle = countZero,
            leadingIcon = Icons.Filled.Home,
            trailingIcon = Icons.Filled.Person
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
            trailingIcon = Icons.Outlined.CheckCircle
        ),
        CollectionCardData(
            title = "Olive Oil",
            subtitle = countTwo,
            leadingIcon = Icons.Filled.Inventory2,
            trailingIcon = Icons.Outlined.CheckCircle
        ),
        CollectionCardData(
            title = "Paprika",
            subtitle = countOne,
            leadingIcon = Icons.Filled.Inventory2,
            trailingIcon = Icons.Outlined.CheckCircle
        )
    )
}

@Composable
private fun defaultProductItems(): List<CollectionCardData> {
    val countTwelve = stringResource(id = R.string.items_count, 12)
    val countSix = stringResource(id = R.string.items_count, 6)
    val countThree = stringResource(id = R.string.items_count, 3)
    return listOf(
        CollectionCardData(
            title = "Seasonal Fruits",
            subtitle = countTwelve,
            leadingIcon = Icons.Rounded.LocalGroceryStore,
            badgeText = stringResource(id = R.string.shared_label),
            trailingIcon = Icons.Outlined.Star
        ),
        CollectionCardData(
            title = "Household Essentials",
            subtitle = countSix,
            leadingIcon = Icons.Filled.Store,
            trailingIcon = Icons.Outlined.Star
        ),
        CollectionCardData(
            title = "Snacks",
            subtitle = countThree,
            leadingIcon = Icons.Filled.Store,
            trailingIcon = Icons.Outlined.Star
        )
    )
}
