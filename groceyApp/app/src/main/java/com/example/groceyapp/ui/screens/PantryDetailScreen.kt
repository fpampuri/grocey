package com.example.groceyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.CategoryCardData
import com.example.groceyapp.ui.components.PantryItemCard
import com.example.groceyapp.ui.components.PantryItemCardData
import com.example.groceyapp.ui.components.general.EmptyState
import com.example.groceyapp.ui.components.general.PrimaryFab
import com.example.groceyapp.ui.components.dialogs.AddPantryItemDialog
import com.example.groceyapp.ui.components.dialogs.ConfirmDeleteDialog
import com.example.groceyapp.data.model.Category
import com.example.groceyapp.data.model.PantryItem
import com.example.groceyapp.data.model.Pantry

/**
 * Detail screen for a pantry
 * Shows the pantry name at the top and all items below with quantity controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryDetailScreen(
    pantryData: CategoryCardData,
    pantries: List<Pantry> = emptyList(),
    categories: List<Category> = emptyList(),
    pantryItems: List<PantryItem> = emptyList(),
    onBackClick: () -> Unit = {},
    onItemQuantityChange: (Int, Double) -> Unit = { _, _ -> },
    onItemMoveToPantry: (Int, Int) -> Unit = { _, _ -> },
    onItemDelete: (Int) -> Unit = {},
    currentDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit = {},
    onRename: (Long?) -> Unit = {},
    onDelete: (Long?) -> Unit = {},
    onAddProduct: (productName: String, categoryId: Int?, quantity: Double) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var showAddProductDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<PantryItemCardData?>(null) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pantryData.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    com.example.groceyapp.ui.components.general.CategoryOptionsMenu(
                        categoryId = pantryData.id,
                        isProtected = pantryData.isProtected,
                        onRename = onRename,
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
                contentDescriptionRes = R.string.add_pantry_product,
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
        // Map API pantry items to UI card data
        val itemsUi = pantryItems.map { item ->
            PantryItemCardData(
                id = item.id ?: 0,
                productName = item.product.name,
                categoryName = item.product.category?.name ?: "",
                quantity = item.quantity
            )
        }

        if (itemsUi.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Filled.Inventory2,
                    messageRes = R.string.empty_pantry_detail_message,
                    hintRes = R.string.empty_pantry_detail_hint
                )
            }
        } else {
            // Pantry items list
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
                        text = "Items",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                // Pantry items
                items(itemsUi, key = { it.id }) { item ->
                    PantryItemCard(
                        data = item,
                        pantries = pantries.filter { it.id?.toLong() != pantryData.id },
                        onQuantityChange = { newQuantity -> 
                            onItemQuantityChange(item.id, newQuantity) 
                        },
                        onMoveToPantry = { newPantryId ->
                            onItemMoveToPantry(item.id, newPantryId)
                        },
                        onDeleteItem = { itemToDelete = item }
                    )
                }
                
                // Bottom padding for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AddPantryItemDialog(
            categories = categories,
            onDismiss = { showAddProductDialog = false },
            onCreate = { productName, categoryId, quantity ->
                onAddProduct(productName, categoryId, quantity)
                showAddProductDialog = false
            }
        )
    }

    // Delete item dialog
    if (itemToDelete != null) {
        ConfirmDeleteDialog(
            title = stringResource(id = R.string.delete),
            message = stringResource(id = R.string.delete_list_item_message, itemToDelete!!.productName),
            onDismiss = { itemToDelete = null },
            onConfirm = {
                onItemDelete(itemToDelete!!.id)
                itemToDelete = null
            }
        )
    }
}
