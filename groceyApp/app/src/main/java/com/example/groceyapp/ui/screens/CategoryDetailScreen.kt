package com.example.groceyapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.EmptyState
import com.example.groceyapp.ui.components.CategoryCardData
import com.example.groceyapp.ui.components.general.PrimaryFab
import com.example.groceyapp.ui.components.ProductItemCard
import com.example.groceyapp.ui.components.ProductItemData
import com.example.groceyapp.ui.components.ProductCardMode

/**
 * Detail screen for a category
 * Shows the category name at the top and all products below
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryData: CategoryCardData,
    onBackClick: () -> Unit = {},
    onProductMenuClick: (String) -> Unit = {},
    onCategoryRename: (Long?) -> Unit = {},
    onCategoryDelete: (Long?) -> Unit = {},
    currentDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryData.title,
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
                    com.example.groceyapp.ui.components.general.CategoryOptionsMenu(
                        categoryId = categoryData.id,
                        onRename = onCategoryRename,
                        onDelete = onCategoryDelete
                    )
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
        // Convert product names to ProductItemData objects
        // In CATEGORY mode, we don't need quantity or isBought
        val productsState = remember {
            mutableStateListOf<ProductItemData>().apply {
                addAll(
                    categoryData.products.map { productName ->
                        ProductItemData(
                            id = productName, // Using name as ID for now
                            name = productName,
                            category = categoryData.title,
                            quantity = 1,
                            isBought = false
                        )
                    }
                )
            }
        }

        if (productsState.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Filled.Store,
                    messageRes = R.string.empty_category_message,
                    hintRes = R.string.empty_category_hint
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

                // Product items in CATEGORY mode (no checkbox, no quantity, with menu)
                items(productsState, key = { it.id }) { product ->
                    ProductItemCard(
                        data = product,
                        mode = ProductCardMode.CATEGORY,
                        onMenuClick = onProductMenuClick
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
