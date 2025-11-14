package com.example.groceyapp.ui.components.general

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Generic collection screen that centralizes search bar, filtering and list layout.
 * T is the item type for the collection; callers provide itemContent to render each row
 * and itemMatchesQuery to define how an item matches the search query.
 */
@Composable
fun <T> GenericCollectionScreen(
    modifier: Modifier = Modifier,
    items: List<T>,
    placeholder: String,
    filterDescription: String,
    showFilterInside: Boolean = false,
    onFilterClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    itemMatchesQuery: (T, String) -> Boolean,
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit,
    emptyIcon: ImageVector? = null,
    @StringRes emptyMessageRes: Int? = null,
    @StringRes emptyHintRes: Int? = null
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter { item -> itemMatchesQuery(item, searchQuery) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            TopSearchBar(
                placeholder = placeholder,
                filterDescription = filterDescription,
                onFilterClick = onFilterClick,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onMenuClick = onMenuClick,
                showFilterInside = showFilterInside
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (filteredItems.isEmpty() && emptyIcon != null && emptyMessageRes != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = emptyIcon,
                        messageRes = emptyMessageRes,
                        hintRes = emptyHintRes
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (itemKey != null) {
                        items(filteredItems, key = itemKey) { item -> itemContent(item) }
                    } else {
                        items(filteredItems) { item -> itemContent(item) }
                    }
                }
            }
        }
    }
}
