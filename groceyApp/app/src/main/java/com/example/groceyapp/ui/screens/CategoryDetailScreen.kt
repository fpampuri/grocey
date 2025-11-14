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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    products: List<com.example.groceyapp.data.model.Product>,
    categories: List<com.example.groceyapp.data.model.Category>,
    onBackClick: () -> Unit = {},
    onProductMoveToCategory: (Int, Int) -> Unit = { _, _ -> },
    onProductAddToList: (String) -> Unit = {},
    onProductAddToPantry: (String) -> Unit = {},
    onProductDelete: (Int) -> Unit = {},
    onProductCreate: (String, Int) -> Unit = { _, _ -> },
    onCategoryRename: (Long?) -> Unit = {},
    onCategoryDelete: (Long?) -> Unit = {},
    currentDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // State for dialogs
    var showMoveCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<com.example.groceyapp.data.model.Product?>(null) }
    
    // Filter products for this category
    val categoryProducts = products.filter { product ->
        product.category?.id == categoryData.id?.toInt()
    }
    
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
                onClick = { showAddProductDialog = true }
            )
        },
        bottomBar = {
            HomeBottomBar(
                currentDestination = currentDestination,
                onDestinationSelected = onDestinationSelected
            )
        }
    ) { paddingValues ->
        if (categoryProducts.isEmpty()) {
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
                items(categoryProducts, key = { it.id ?: 0 }) { product ->
                    ProductItemCard(
                        data = ProductItemData(
                            id = product.id?.toString() ?: "",
                            name = product.name,
                            category = product.category?.name ?: "",
                            quantity = 1,
                            isBought = false
                        ),
                        mode = ProductCardMode.CATEGORY,
                        onMoveToCategory = {
                            selectedProduct = product
                            showMoveCategoryDialog = true
                        },
                        onAddToList = { onProductAddToList(product.id?.toString() ?: "") },
                        onAddToPantry = { onProductAddToPantry(product.id?.toString() ?: "") },
                        onDeleteProduct = {
                            selectedProduct = product
                            showDeleteDialog = true
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
    
    // Move to Category Dialog (outside Scaffold)
    if (showMoveCategoryDialog && selectedProduct != null) {
        com.example.groceyapp.ui.components.dialogs.SelectCategoryDialog(
            productName = selectedProduct!!.name,
            categories = categories,
            currentCategoryId = selectedProduct!!.category?.id,
            onDismiss = {
                showMoveCategoryDialog = false
                selectedProduct = null
            },
            onCategorySelected = { newCategoryId ->
                selectedProduct?.id?.let { productId ->
                    onProductMoveToCategory(productId, newCategoryId)
                }
                showMoveCategoryDialog = false
                selectedProduct = null
            }
        )
    }
    
    // Delete Product Dialog (outside Scaffold)
    if (showDeleteDialog && selectedProduct != null) {
        com.example.groceyapp.ui.components.dialogs.ConfirmDeleteDialog(
            title = stringResource(id = R.string.delete_product),
            message = stringResource(id = R.string.delete_product_message, selectedProduct!!.name),
            onDismiss = {
                showDeleteDialog = false
                selectedProduct = null
            },
            onConfirm = {
                selectedProduct?.id?.let { productId ->
                    onProductDelete(productId)
                }
                showDeleteDialog = false
                selectedProduct = null
            }
        )
    }
    
    // Add Product Dialog (outside Scaffold)
    if (showAddProductDialog) {
        com.example.groceyapp.ui.components.dialogs.AddProductToCategoryDialog(
            categoryName = categoryData.title,
            onDismiss = { showAddProductDialog = false },
            onConfirm = { productName ->
                categoryData.id?.toInt()?.let { categoryId ->
                    onProductCreate(productName, categoryId)
                }
                showAddProductDialog = false
            }
        )
    }
}
