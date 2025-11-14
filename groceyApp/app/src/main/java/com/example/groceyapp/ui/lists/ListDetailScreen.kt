package com.example.groceyapp.ui.lists

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.EmptyState
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.PrimaryFab
import com.example.groceyapp.ui.components.ProductItemCard
import com.example.groceyapp.ui.components.ProductItemData

/**
 * Detail screen for a shopping list
 * Shows the list name at the top and all products below with their controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listData: ListCardData,
    onBackClick: () -> Unit = {},
    onProductToggle: (String) -> Unit = {},
    onQuantityChange: (String, Int) -> Unit = { _, _ -> },
    currentDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = listData.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.menu_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            PrimaryFab(
                contentDescriptionRes = R.string.add_product,
                onClick = { /* TODO: Add product action */ }
            )
        },
        bottomBar = {
            HomeBottomBar(
                currentDestination = currentDestination,
                onDestinationSelected = onDestinationSelected
            )
        }
    ) { paddingValues ->
        // Use a local, mutable list state initialized from the passed-in model so
        // the UI is immediately interactive. This is intentionally simple so it
        // can be replaced later by a ViewModel/Repository without changing the UI.
        val productsState = remember { mutableStateListOf<ProductItemData>().apply { addAll(listData.products) } }

        if (productsState.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Filled.ShoppingCart,
                    messageRes = R.string.empty_list_detail_message,
                    hintRes = R.string.empty_list_detail_hint
                )
            }
        } else {
            // Products list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section header
                item {
                    Text(
                        text = "Products",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                // Product items
                // API CHANGE: Persistence for product state (bought flag, quantity) should be
                // handled by a ViewModel/Repository. Wire `onToggleBought` and `onQuantityChange`
                // to the ListDetailViewModel (e.g. `viewModel.toggleBought(productId)`,
                // `viewModel.setQuantity(productId, q)`) so changes persist in the DB/API.
                items(productsState, key = { it.id }) { product ->
                    ProductItemCard(
                        data = product,
                        onToggleBought = {
                            // Update local UI state immediately
                            val idx = productsState.indexOfFirst { it.id == product.id }
                            if (idx >= 0) {
                                productsState[idx] = productsState[idx].copy(isBought = !productsState[idx].isBought)
                            }
                            // API CHANGE: Persist this change via ViewModel/Repository. Example:
                            // `listDetailViewModel.toggleBought(product.id)`
                            onProductToggle(product.id)
                        },
                        onQuantityChange = { newQuantity ->
                            // Update local UI state immediately
                            val idx = productsState.indexOfFirst { it.id == product.id }
                            if (idx >= 0) {
                                productsState[idx] = productsState[idx].copy(quantity = newQuantity)
                            }
                            // API CHANGE: Persist this change via ViewModel/Repository. Example:
                            // `listDetailViewModel.setQuantity(product.id, newQuantity)`
                            onQuantityChange(product.id, newQuantity)
                        }
                    )
                }
                
                // Bottom padding for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
