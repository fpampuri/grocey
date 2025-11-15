package com.example.groceyapp.ui.screens

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
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.general.PrimaryFab
import com.example.groceyapp.ui.components.ProductItemCard
import com.example.groceyapp.ui.components.ProductItemData
import com.example.groceyapp.ui.components.dialogs.ConfirmDeleteDialog
import com.example.groceyapp.data.model.Category
import com.example.groceyapp.data.model.ListItem
import com.example.groceyapp.ui.components.dialogs.AddProductDialog

/**
 * Detail screen for a shopping list
 * Shows the list name at the top and all products below with their controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listData: ListCardData,
    categories: List<Category> = emptyList(),
    listItems: List<ListItem> = emptyList(),
    onBackClick: () -> Unit = {},
    onItemTogglePurchased: (Int) -> Unit = {},
    onItemQuantityChange: (Int, Int) -> Unit = { _, _ -> },
    onItemDelete: (Int) -> Unit = {},
    currentDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit = {},
    onRename: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onAddProduct: (name: String, categoryId: Int?) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showAddProductDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ProductItemData?>(null) }
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
                    com.example.groceyapp.ui.components.general.ListOptionsMenu(
                        itemId = listData.id,
                        onRename = onRename,
                        onShare = onShare,
                        onDelete = onDelete
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
        // Map API list items to UI card data
        val productsUi = listItems.map { item ->
            ProductItemData(
                id = (item.id ?: 0).toString(),
                name = item.product.name,
                category = item.product.category?.name ?: "",
                quantity = item.quantity.toInt(),
                isBought = item.purchased == true
            )
        }

        if (productsUi.isEmpty()) {
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
                items(productsUi, key = { it.id }) { product ->
                    val itemId = product.id.toIntOrNull() ?: 0
                    ProductItemCard(
                        data = product,
                        onToggleBought = { onItemTogglePurchased(itemId) },
                        onQuantityChange = { newQuantity -> onItemQuantityChange(itemId, newQuantity) },
                        onDeleteItem = { itemToDelete = product }
                    )
                }
                
                // Bottom padding for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add Product Dialog (outside Scaffold)
    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onProductSelected = { productName, categoryId ->
                onAddProduct(productName, categoryId)
                showAddProductDialog = false
            },
            categories = categories
        )
    }

    // Delete item dialog
    if (itemToDelete != null) {
        ConfirmDeleteDialog(
            title = stringResource(id = R.string.delete),
            message = stringResource(id = R.string.delete_list_item_message, itemToDelete!!.name),
            onDismiss = { itemToDelete = null },
            onConfirm = {
                val itemId = itemToDelete!!.id.toIntOrNull() ?: return@ConfirmDeleteDialog
                onItemDelete(itemId)
                itemToDelete = null
            }
        )
    }
}
