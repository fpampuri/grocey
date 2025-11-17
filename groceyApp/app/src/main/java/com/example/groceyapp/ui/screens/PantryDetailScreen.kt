package com.example.groceyapp.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Inventory2
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.groceyapp.ui.components.general.FabWithFloatingNav
import com.example.groceyapp.ui.components.dialogs.AddPantryItemDialog
import com.example.groceyapp.ui.components.dialogs.ConfirmDeleteDialog
import com.example.groceyapp.data.model.Category
import com.example.groceyapp.data.model.PantryItem
import com.example.groceyapp.data.model.Pantry
import com.example.groceyapp.ui.components.general.AdaptiveNavigationContainer
import com.example.groceyapp.ui.screens.HomeFloatingNav

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
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    var showAddProductDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<PantryItemCardData?>(null) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val useRailNavigation = isLandscape && !isTablet
    val showBottomNav = !isTablet && !isLandscape
    val showFloatingNav = isTablet
    
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
            val fabContent: @Composable () -> Unit = {
                PrimaryFab(
                    contentDescriptionRes = R.string.add_pantry_product,
                    onClick = { showAddProductDialog = true }
                )
            }
            if (showFloatingNav) {
                FabWithFloatingNav(
                    fab = fabContent,
                    nav = {
                        HomeFloatingNav(
                            currentDestination = currentDestination,
                            onDestinationSelected = onDestinationSelected
                        )
                    }
                )
            } else {
                fabContent()
            }
        },
        bottomBar = {
            if (showBottomNav) {
                HomeBottomBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = onDestinationSelected
                )
            }
        }
    ) { paddingValues ->
        // Map API pantry items to UI card data, filtering out items with deleted products
        val itemsUi = pantryItems.mapNotNull { item ->
            // Skip items where the product has been deleted
            if (item.product == null) return@mapNotNull null
            
            PantryItemCardData(
                id = item.id ?: 0,
                productName = item.product.name,
                categoryName = item.product.category?.name ?: "",
                quantity = item.quantity
            )
        }

        val contentArea: @Composable (Modifier) -> Unit = { contentModifier ->
            if (itemsUi.isEmpty()) {
                // Empty state
                Box(
                    modifier = contentModifier,
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
                    modifier = contentModifier,
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

        AdaptiveNavigationContainer(
            isLandscape = useRailNavigation,
            paddingValues = paddingValues,
            navigationRail = if (useRailNavigation) {
                {
                    HomeBottomBar(
                        currentDestination = currentDestination,
                        onDestinationSelected = onDestinationSelected,
                        isVertical = true
                    )
                }
            } else null,
            content = contentArea
        )
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
