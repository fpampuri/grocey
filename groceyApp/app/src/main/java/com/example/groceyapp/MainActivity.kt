package com.example.groceyapp

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.material3.SnackbarDuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.groceyapp.R
import com.example.groceyapp.ui.auth.AuthenticationScreen
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.CategoryCardData
import com.example.groceyapp.ui.components.general.MenuDrawer
import com.example.groceyapp.ui.components.general.PrimaryFab
import com.example.groceyapp.ui.components.general.FabWithFloatingNav
import com.example.groceyapp.ui.components.dialogs.CreateListDialog
import com.example.groceyapp.ui.screens.HomeBottomBar
import com.example.groceyapp.ui.screens.HomeFloatingNav
import com.example.groceyapp.ui.screens.HomeDestination
import com.example.groceyapp.ui.screens.ListDetailScreen
import com.example.groceyapp.ui.screens.CategoryDetailScreen
import com.example.groceyapp.ui.screens.ListsScreen
import com.example.groceyapp.ui.screens.PantryScreen
import com.example.groceyapp.ui.screens.ProductsScreen
import com.example.groceyapp.ui.components.dialogs.CreateCategoryDialog
import com.example.groceyapp.ui.components.dialogs.CreateProductDialog
import com.example.groceyapp.ui.components.dialogs.ConfirmDeleteDialog
import com.example.groceyapp.ui.components.dialogs.RenameDialog
import com.example.groceyapp.ui.viewmodel.AuthViewModel
import com.example.groceyapp.ui.viewmodel.ProductViewModel
import com.example.groceyapp.ui.viewmodel.ShoppingListViewModel
import com.example.groceyapp.ui.viewmodel.PantryViewModel
import com.example.groceyapp.ui.viewmodel.PasswordChangeState
import com.example.groceyapp.ui.utils.mapIconToString
import com.example.groceyapp.ui.utils.mapStringToIcon
import com.example.groceyapp.utils.LocaleHelper
import com.example.groceyapp.ui.screens.PantryDetailScreen
import com.example.groceyapp.ui.components.dialogs.CreatePantryDialog
import com.example.groceyapp.ui.components.dialogs.EditPantryDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.MaterialTheme
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.groceyapp.ui.components.general.AdaptiveNavigationContainer
import com.example.groceyapp.ui.components.general.PasswordChangeDialog
import com.example.groceyapp.ui.theme.GroceyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply saved locale
        LocaleHelper.onActivityCreated(this)
        
        enableEdgeToEdge()
        setContent {
            ListsApp()
        }
    }
}

@Composable
fun ListsApp() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.smallestScreenWidthDp >= 600
    val showFloatingNav = isTablet
    val showRailNav = !isTablet && isLandscape
    val showBottomNav = !isTablet && !isLandscape
    
    // Get ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val shoppingListViewModel: ShoppingListViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val pantryViewModel: PantryViewModel = viewModel()
    
    // Collect authentication state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val passwordChangeState by authViewModel.passwordChangeState.collectAsState()
    
    // Restore session when app starts (only runs once)
    LaunchedEffect(Unit) {
        authViewModel.restoreSession()
    }
    
    var currentDestination by rememberSaveable { mutableStateOf(HomeDestination.Lists) }
    var selectedListId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedPantryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var isMenuOpen by remember { mutableStateOf(LocaleHelper.shouldOpenMenuOnStart(context)) }
    
    // Settings state
    val systemDark = isSystemInDarkTheme()
    var isDarkMode by rememberSaveable { mutableStateOf(systemDark) }
    var currentLanguage by remember { mutableStateOf(LocaleHelper.getCurrentLocale(context)) }
    
    // Shopping lists from API
    val apiLists by shoppingListViewModel.lists.collectAsState()
    val itemCounts by shoppingListViewModel.listItemCounts.collectAsState()
    val pantryItemCounts by pantryViewModel.pantryItemCounts.collectAsState()
    var showCreateListDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }
    var showRenameListDialog by remember { mutableStateOf(false) }
    var showShareListDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<Int?>(null) }
    var listToRename by remember { mutableStateOf<Triple<Int, String, androidx.compose.ui.graphics.vector.ImageVector?>?>(null) }
    var listToShare by remember { mutableStateOf<Pair<Int, String>?>(null) }
    
    // Products and categories dialog states
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var showProductFabMenu by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Int?>(null) }
    var categoryToEdit by remember { mutableStateOf<Triple<Int, String, androidx.compose.ui.graphics.vector.ImageVector?>?>(null) }
    
    // Pantry dialog states
    var showCreatePantryDialog by remember { mutableStateOf(false) }
    var showDeletePantryDialog by remember { mutableStateOf(false) }
    var showEditPantryDialog by remember { mutableStateOf(false) }
    var pantryToDelete by remember { mutableStateOf<Int?>(null) }
    var pantryToEdit by remember { mutableStateOf<Triple<Int, String, androidx.compose.ui.graphics.vector.ImageVector?>?>(null) }
    
    // Products and categories from API
    val products by productViewModel.products.collectAsState()
    val categories by productViewModel.categories.collectAsState()
    val productSearchQuery by productViewModel.searchQuery.collectAsState()
    
    // Pantries and pantry items from API
    val pantries by pantryViewModel.pantries.collectAsState()
    val pantryItems by pantryViewModel.pantryItems.collectAsState()
    val pantrySearchQuery by pantryViewModel.searchQuery.collectAsState()
    
    // Shopping list search query
    val listSearchQuery by shoppingListViewModel.searchQuery.collectAsState()

    GroceyAppTheme(darkTheme = isDarkMode) {
        // Snackbar + coroutine scope for showing feedback
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        var lastSnackbarIsSuccess by remember { mutableStateOf(false) }
        val errorMessage by shoppingListViewModel.errorMessage.collectAsState()
        val listItems by shoppingListViewModel.listItems.collectAsState()

        // Show error messages from ViewModel in a Snackbar
        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                // mark as error
                lastSnackbarIsSuccess = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short)
                }
                shoppingListViewModel.clearError()
            }
        }
    
        // Load shopping lists when authenticated
        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated) {
                shoppingListViewModel.loadShoppingLists()
                productViewModel.loadProducts()
                productViewModel.loadCategories()
                pantryViewModel.loadPantries()
            }
        }
    
        LaunchedEffect(passwordChangeState, showPasswordDialog) {
            if (showPasswordDialog && passwordChangeState is PasswordChangeState.Success) {
                showPasswordDialog = false
                authViewModel.resetPasswordChangeState()
                lastSnackbarIsSuccess = true
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.password_change_success),
                    duration = SnackbarDuration.Short
                )
                lastSnackbarIsSuccess = false
            }
        }
    
        // Convert API lists to UI format
        val lists = apiLists.map { apiList ->
            // Extract icon from metadata, default to shopping cart
            val iconName = apiList.metadata?.get("icon") as? String
            Log.d("MainActivity", "List ${apiList.name} has icon metadata: $iconName")
            val icon = mapStringToIcon(iconName)
            
            ListCardData(
                id = apiList.id?.toString() ?: "",
                title = apiList.name,
                itemCount = apiList.id?.let { itemCounts[it] } ?: 0,
                leadingIcon = icon,
                isFavorite = false,
                isShared = apiList.sharedWith?.isNotEmpty() == true,
                products = emptyList()
            )
        }

    // Convert API categories and products to UI format
    val categoryCards = categories.map { category ->
        // Extract icon from metadata and map to ImageVector
        val iconName = category.metadata?.get("icon") as? String
        val icon = mapStringToIcon(iconName)
        
        // Get products for this category
        val categoryProducts = products
            .filter { it.category?.id == category.id }
            .map { it.name }
        
        com.example.groceyapp.ui.components.CategoryCardData(
            id = category.id?.toLong(),
            title = category.name,
            subtitle = "${categoryProducts.size} products",
            leadingIcon = icon,
            products = categoryProducts,
            isProtected = category.name.equals(Constants.MISCELLANEOUS_CATEGORY_NAME, ignoreCase = true) ||
                ((category.metadata?.get(Constants.MISC_CATEGORY_META_KEY) as? String)
                    ?.equals(Constants.MISC_CATEGORY_META_VALUE, ignoreCase = true) == true)
        )
    }
    
    // Convert API pantries to UI format
    val pantryCards = pantries.map { pantry ->
        // Extract icon from metadata
        val iconName = pantry.metadata?.get("icon") as? String
        Log.d("MainActivity", "Pantry ${pantry.name} has icon metadata: $iconName, id: ${pantry.id}")
        val icon = mapStringToIcon(iconName)
        
        // Get item count for this pantry
        val itemCount = pantry.id?.let { pantryItemCounts[it] } ?: 0
        Log.d("MainActivity", "Pantry ${pantry.name} (id: ${pantry.id}) item count: $itemCount, pantryItemCounts: $pantryItemCounts")
        val subtitle = if (itemCount == 1) "$itemCount product" else "$itemCount products"
        
        // For pantries, we show them as cards like categories
        com.example.groceyapp.ui.components.CategoryCardData(
            id = pantry.id?.toLong(),
            title = pantry.name,
            subtitle = subtitle,
            leadingIcon = icon,
            products = emptyList(),
            isProtected = false
        )
    }
    
    Log.d("MainActivity", "Total pantries loaded: ${pantries.size}, pantryCards: ${pantryCards.size}")

    val selectedCategoryCard = selectedCategoryId?.let { id ->
        categoryCards.find { it.id == id }
    }

    val selectedPantryCard = selectedPantryId?.let { id ->
        pantryCards.find { it.id == id }
    }

        // Show auth screen if not authenticated OR user profile not loaded yet
        if (!isAuthenticated || currentUser == null) {
            AuthenticationScreen(
                onLoginSuccess = {
                    // Authentication handled by viewModel
                    // User will be automatically authenticated when login succeeds
                }
            )
            return@GroceyAppTheme
        }

        // Extract user info from currentUser
        val userEmail = currentUser?.email ?: ""
        val userName = "${currentUser?.name ?: ""} ${currentUser?.surname ?: ""}"

        // Determine if we're showing list detail
        val selectedList: ListCardData? = selectedListId?.let { id ->
            lists.find { list -> list.id == id }
        }

        BackHandler(enabled = selectedList != null || selectedCategoryCard != null || selectedPantryCard != null) {
            when {
                selectedPantryCard != null -> selectedPantryId = null
                selectedCategoryCard != null -> selectedCategoryId = null
                selectedList != null -> selectedListId = null
            }
        }

        // When a list is selected, load its items from API
        LaunchedEffect(selectedList?.id) {
            val listIdInt = selectedList?.id?.toIntOrNull()
            if (listIdInt != null) {
                shoppingListViewModel.loadListItems(listIdInt)
            }
        }
        
        // When a pantry is selected, load its items from API
        LaunchedEffect(selectedPantryId) {
            val pantryIdInt = selectedPantryId?.toInt()
            if (pantryIdInt != null) {
                pantryViewModel.loadPantryItems(pantryIdInt)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedList != null) {
                // Show list detail screen
                ListDetailScreen(
                    listData = selectedList,
                    categories = categories,
                    listItems = listItems,
                    isTablet = isTablet,
                    onBackClick = { selectedListId = null },
                    onItemTogglePurchased = { itemId ->
                        val listIdInt = selectedList.id.toIntOrNull()
                        if (listIdInt != null) {
                            val currentPurchased = listItems.find { it.id == itemId }?.purchased ?: false
                            shoppingListViewModel.markItemAsPurchased(listIdInt, itemId, !currentPurchased)
                        }
                    },
                    onItemQuantityChange = { itemId, newQty ->
                        val listIdInt = selectedList.id.toIntOrNull()
                        if (listIdInt != null) {
                            shoppingListViewModel.updateItemQuantity(listIdInt, itemId, newQty.toDouble())
                        }
                    },
                    onItemDelete = { itemId ->
                        val listIdInt = selectedList.id.toIntOrNull()
                        if (listIdInt != null) {
                            shoppingListViewModel.removeItemFromList(listIdInt, itemId) {
                                // Optional: show snackbar
                                coroutineScope.launch {
                                    lastSnackbarIsSuccess = false
                                    snackbarHostState.showSnackbar(
                                        message = "Item removed",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    },
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        // Clear search when switching tabs
                        when (destination) {
                            HomeDestination.Lists -> shoppingListViewModel.loadShoppingLists(null)
                            HomeDestination.Products -> productViewModel.searchCategoriesAndProducts(null)
                            HomeDestination.Pantry -> pantryViewModel.searchPantries("")
                        }
                        currentDestination = destination
                        selectedListId = null  // Go back to main view when switching tabs
                    },
                    onRename = { listId ->
                        val list = apiLists.find { it.id.toString() == listId }
                        val uiList = lists.find { it.id == listId }
                        list?.id?.let { id ->
                            listToRename = Triple(id, list.name, uiList?.leadingIcon)
                            showRenameListDialog = true
                        }
                    },
                    onDelete = { listId ->
                        val list = apiLists.find { it.id.toString() == listId }
                        list?.id?.let { id ->
                            listToDelete = id
                            showDeleteListDialog = true
                        }
                    },
                    onShare = { listId ->
                        val list = apiLists.find { it.id.toString() == listId }
                        list?.id?.let { id ->
                            listToShare = Pair(id, list.name)
                            showShareListDialog = true
                        }
                    },
                    onAddProduct = { productName, maybeCategoryId ->
                        // Resolve target category: use provided, else fallback to default Miscellaneous
                        val resolvedCategoryId: Int? = maybeCategoryId ?: run {
                            val misc = categories.find { cat ->
                                cat.name.equals(Constants.MISCELLANEOUS_CATEGORY_NAME, ignoreCase = true) ||
                                    ((cat.metadata?.get(Constants.MISC_CATEGORY_META_KEY) as? String)
                                        ?.equals(Constants.MISC_CATEGORY_META_VALUE, ignoreCase = true) == true)
                            }
                            misc?.id
                        }

                        // Create product, then add as list item
                        productViewModel.createProduct(
                            name = productName,
                            categoryId = resolvedCategoryId,
                            onSuccess = { createdProduct ->
                                val listIdInt = selectedList.id.toIntOrNull()
                                if (listIdInt != null && createdProduct.id != null) {
                                    // Add a small delay to ensure the product is fully persisted in the backend
                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(100) // 100ms delay
                                        shoppingListViewModel.addItemToList(
                                            listId = listIdInt,
                                            productId = createdProduct.id,
                                            onSuccess = {
                                                // Show success feedback
                                                coroutineScope.launch {
                                                    lastSnackbarIsSuccess = true
                                                    val msg = context.getString(R.string.product_created, createdProduct.name)
                                                    snackbarHostState.showSnackbar(
                                                        message = msg,
                                                        duration = SnackbarDuration.Short
                                                    )
                                                    lastSnackbarIsSuccess = false
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
        } else if (selectedCategoryCard != null) {
            // Show category detail screen
            val category = selectedCategoryCard!!
            CategoryDetailScreen(
                categoryData = category,
                products = products,
                categories = categories,
                lists = apiLists,
                pantries = pantries,
                isTablet = isTablet,
                onBackClick = { selectedCategoryId = null },
                onProductUpdate = { productId, newName ->
                    // Find the product to get its current category
                    val product = products.find { it.id == productId }
                    product?.let {
                        productViewModel.updateProduct(
                            id = productId,
                            name = newName,
                            categoryId = it.category?.id,
                            onSuccess = {
                                productViewModel.loadProducts()
                                // Show success feedback
                                coroutineScope.launch {
                                    lastSnackbarIsSuccess = true
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.product_updated, newName),
                                        duration = SnackbarDuration.Short
                                    )
                                    lastSnackbarIsSuccess = false
                                }
                            }
                        )
                    }
                },
                onProductMoveToCategory = { productId, newCategoryId ->
                    // Find the product to get its current name
                    val product = products.find { it.id == productId }
                    product?.let {
                        productViewModel.updateProduct(
                            id = productId,
                            name = it.name,  // Include the product name
                            categoryId = newCategoryId,
                            onSuccess = {
                                productViewModel.loadProducts()
                                // Show success feedback
                                coroutineScope.launch {
                                    lastSnackbarIsSuccess = true
                                    snackbarHostState.showSnackbar(
                                        message = "Product moved successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                    lastSnackbarIsSuccess = false
                                }
                            }
                        )
                    }
                },
                onProductAddToList = { listId, productId, quantity ->
                    shoppingListViewModel.addItemToList(
                        listId = listId,
                        productId = productId,
                        quantity = quantity,
                        onSuccess = {
                            // Show success feedback
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.pantry_item_added),
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                },
                onProductAddToPantry = { pantryId, productId, quantity ->
                    pantryViewModel.addItemToPantry(
                        pantryId = pantryId,
                        productId = productId,
                        quantity = quantity,
                        onSuccess = {
                            // Show success feedback
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.pantry_item_added),
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                },
                onProductDelete = { productId ->
                    productViewModel.deleteProduct(
                        id = productId,
                        onSuccess = {
                            productViewModel.loadProducts()
                            // Show success feedback
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = false
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.item_removed),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                },
                onProductCreate = { productName, categoryId ->
                    productViewModel.createProduct(
                        name = productName,
                        categoryId = categoryId,
                        onSuccess = {
                            productViewModel.loadProducts()
                            // Show success feedback
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.product_moved),
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                },
                onCategoryRename = { categoryId ->
                    categoryId?.let { catId ->
                        val apiCategory = categories.find { cat -> cat.id == catId.toInt() }
                        val categoryCard = categoryCards.find { card -> card.id == catId }
                        apiCategory?.let { cat ->
                            cat.id?.let { id ->
                                categoryToEdit = Triple(id, cat.name, categoryCard?.leadingIcon)
                                showEditCategoryDialog = true
                            }
                        }
                    }
                },
                onCategoryDelete = { categoryId ->
                    categoryId?.let {
                        categoryToDelete = it.toInt()
                        showDeleteCategoryDialog = true
                    }
                },
                currentDestination = currentDestination,
                onDestinationSelected = { destination ->
                    // Clear search when switching tabs
                    when (destination) {
                        HomeDestination.Lists -> shoppingListViewModel.loadShoppingLists(null)
                        HomeDestination.Products -> productViewModel.searchCategoriesAndProducts(null)
                        HomeDestination.Pantry -> pantryViewModel.searchPantries("")
                    }
                    currentDestination = destination
                    selectedCategoryId = null  // Go back to main view when switching tabs
                }
            )
        } else if (selectedPantryCard != null) {
            // Show pantry detail screen
            val pantry = selectedPantryCard!!
            PantryDetailScreen(
                pantryData = pantry,
                pantries = pantries,
                categories = categories,
                pantryItems = pantryItems,
                isTablet = isTablet,
                onBackClick = { selectedPantryId = null },
                onItemQuantityChange = { itemId, newQty ->
                    val pantryIdInt = pantry.id?.toInt()
                    if (pantryIdInt != null) {
                        pantryViewModel.updateItemQuantity(pantryIdInt, itemId, newQty)
                    }
                },
                onItemMoveToPantry = { itemId, newPantryId ->
                    val currentPantryId = pantry.id?.toInt()
                    if (currentPantryId != null) {
                        // Find the item to get its product ID and current quantity
                        val item = pantryItems.find { it.id == itemId }
                        item?.let {
                            // Check if product still exists
                            if (it.product == null) {
                                coroutineScope.launch {
                                    lastSnackbarIsSuccess = false
                                    snackbarHostState.showSnackbar(
                                        message = "Cannot move item: product no longer exists",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                return@let
                            }
                            
                            // Remove from current pantry
                            pantryViewModel.removeItemFromPantry(currentPantryId, itemId) {
                                // Add to new pantry
                                pantryViewModel.addItemToPantry(
                                    pantryId = newPantryId,
                                    productId = it.product.id ?: return@removeItemFromPantry,
                                    quantity = it.quantity,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            lastSnackbarIsSuccess = true
                                            snackbarHostState.showSnackbar(
                                                message = "Item moved to pantry",
                                                duration = SnackbarDuration.Short
                                            )
                                            lastSnackbarIsSuccess = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                onItemDelete = { itemId ->
                    val pantryIdInt = pantry.id?.toInt()
                    if (pantryIdInt != null) {
                        pantryViewModel.removeItemFromPantry(pantryIdInt, itemId) {
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = false
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.item_removed),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                },
                currentDestination = currentDestination,
                onDestinationSelected = { destination ->
                    // Clear search when switching tabs
                    when (destination) {
                        HomeDestination.Lists -> shoppingListViewModel.loadShoppingLists(null)
                        HomeDestination.Products -> productViewModel.searchCategoriesAndProducts(null)
                        HomeDestination.Pantry -> pantryViewModel.searchPantries("")
                    }
                    currentDestination = destination
                    selectedPantryId = null
                },
                onRename = { pantryId ->
                    pantryId?.let { id ->
                        val apiPantry = pantries.find { p -> p.id == id.toInt() }
                        val pantryCard = pantryCards.find { card -> card.id == id }
                        apiPantry?.let { p ->
                            p.id?.let { pId ->
                                pantryToEdit = Triple(pId, p.name, pantryCard?.leadingIcon)
                                showEditPantryDialog = true
                            }
                        }
                    }
                },
                onDelete = { pantryId ->
                    pantryId?.let {
                        pantryToDelete = it.toInt()
                        showDeletePantryDialog = true
                    }
                },
                onAddProduct = { productName, maybeCategoryId, quantity ->
                    // Resolve category
                    val resolvedCategoryId: Int? = maybeCategoryId ?: run {
                        val misc = categories.find { cat ->
                            cat.name.equals(Constants.MISCELLANEOUS_CATEGORY_NAME, ignoreCase = true) ||
                                ((cat.metadata?.get(Constants.MISC_CATEGORY_META_KEY) as? String)
                                    ?.equals(Constants.MISC_CATEGORY_META_VALUE, ignoreCase = true) == true)
                        }
                        misc?.id
                    }
                    
                    // Create product, then add as pantry item
                    productViewModel.createProduct(
                        name = productName,
                        categoryId = resolvedCategoryId,
                        onSuccess = { createdProduct ->
                            val pantryIdInt = pantry.id?.toInt()
                            if (pantryIdInt != null && createdProduct.id != null) {
                                pantryViewModel.addItemToPantry(
                                    pantryId = pantryIdInt,
                                    productId = createdProduct.id,
                                    quantity = quantity,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            lastSnackbarIsSuccess = true
                                            val msg = context.getString(R.string.pantry_item_added, createdProduct.name)
                                            snackbarHostState.showSnackbar(
                                                message = msg,
                                                duration = SnackbarDuration.Short
                                            )
                                            lastSnackbarIsSuccess = false
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            )
        } else {
            // Show main app with bottom navigation
            Scaffold(
                snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = if (lastSnackbarIsSuccess) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                            )
                        }
                },
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    val fabContent: @Composable () -> Unit = {
                        when (currentDestination) {
                            HomeDestination.Pantry -> {
                                PrimaryFab(
                                    contentDescriptionRes = R.string.add_pantry,
                                    onClick = { showCreatePantryDialog = true }
                                )
                            }
                            HomeDestination.Products -> {
                                Box {
                                    PrimaryFab(
                                        contentDescriptionRes = R.string.add_product,
                                        onClick = { showProductFabMenu = true }
                                    )
                                    DropdownMenu(
                                        expanded = showProductFabMenu,
                                        onDismissRequest = { showProductFabMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(id = R.string.add_product)) },
                                            onClick = {
                                                showProductFabMenu = false
                                                showCreateProductDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(id = R.string.add_category)) },
                                            onClick = {
                                                showProductFabMenu = false
                                                showCreateCategoryDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                            HomeDestination.Lists -> {
                                PrimaryFab(
                                    contentDescriptionRes = R.string.add_list,
                                    onClick = { showCreateListDialog = true }
                                )
                            }
                        }
                    }
                    if (showFloatingNav) {
                        FabWithFloatingNav(
                            fab = fabContent,
                            nav = {
                                HomeFloatingNav(
                                    currentDestination = currentDestination,
                                    onDestinationSelected = { destination ->
                                        // Clear search when switching tabs
                                        when (destination) {
                                            HomeDestination.Lists -> shoppingListViewModel.loadShoppingLists(null)
                                            HomeDestination.Products -> productViewModel.searchCategoriesAndProducts(null)
                                            HomeDestination.Pantry -> pantryViewModel.searchPantries("")
                                        }
                                        currentDestination = destination
                                    }
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
                            onDestinationSelected = { destination ->
                                // Clear search when switching tabs
                                when (destination) {
                                    HomeDestination.Lists -> shoppingListViewModel.loadShoppingLists(null)
                                    HomeDestination.Products -> productViewModel.searchCategoriesAndProducts(null)
                                    HomeDestination.Pantry -> pantryViewModel.searchPantries("")
                                }
                                currentDestination = destination
                            }
                        )
                    }
                }
            ) { innerPadding ->
                val renderDestination: @Composable (Modifier) -> Unit = { contentModifier ->
                    when (currentDestination) {
                        HomeDestination.Pantry -> PantryScreen(
                            modifier = contentModifier,
                            items = pantryCards,
                            searchQuery = pantrySearchQuery,
                            onMenuClick = { isMenuOpen = true },
                            onPantryDelete = { pantryId ->
                                pantryId?.let {
                                    pantryToDelete = it.toInt()
                                    showDeletePantryDialog = true
                                }
                            },
                            onPantryRename = { pantryId ->
                                pantryId?.let { id ->
                                    val pantry = pantries.find { p -> p.id == id.toInt() }
                                    val pantryCard = pantryCards.find { card -> card.id == id }
                                    pantry?.let { p ->
                                        p.id?.let { pId ->
                                            pantryToEdit = Triple(pId, p.name, pantryCard?.leadingIcon)
                                            showEditPantryDialog = true
                                        }
                                    }
                                }
                            },
                            onPantryClick = { pantryData ->
                            selectedPantryId = pantryData.id
                            },
                            onSearchQueryChange = { query ->
                                pantryViewModel.searchPantries(query)
                            }
                        )
                        HomeDestination.Products -> ProductsScreen(
                            modifier = contentModifier,
                            items = categoryCards,
                            searchQuery = productSearchQuery,
                            onMenuClick = { isMenuOpen = true },
                            onCategoryDelete = { categoryId ->
                                categoryId?.let {
                                    categoryToDelete = it.toInt()
                                    showDeleteCategoryDialog = true
                                }
                            },
                            onCategoryRename = { categoryId ->
                                categoryId?.let { catId ->
                                    val category = categories.find { cat -> cat.id == catId.toInt() }
                                    val categoryCard = categoryCards.find { card -> card.id == catId }
                                    category?.let { cat ->
                                        cat.id?.let { id ->
                                            categoryToEdit = Triple(id, cat.name, categoryCard?.leadingIcon)
                                            showEditCategoryDialog = true
                                        }
                                    }
                                }
                            },
                            onCategoryClick = { categoryData ->
                                selectedCategoryId = categoryData.id
                            },
                            onSearchQueryChange = { query ->
                                productViewModel.searchCategoriesAndProducts(query.ifBlank { null })
                            }
                        )
                        HomeDestination.Lists -> ListsScreen(
                            modifier = contentModifier,
                            items = lists,
                            searchQuery = listSearchQuery,
                            onListClick = { listId -> selectedListId = listId },
                            onMenuClick = { isMenuOpen = true },
                            onRename = { listId ->
                                val list = apiLists.find { it.id.toString() == listId }
                                val uiList = lists.find { it.id == listId }
                                list?.id?.let { id ->
                                    listToRename = Triple(id, list.name, uiList?.leadingIcon)
                                    showRenameListDialog = true
                                }
                            },
                            onDelete = { listId ->
                                val list = apiLists.find { it.id.toString() == listId }
                                list?.id?.let { id ->
                                    listToDelete = id
                                    showDeleteListDialog = true
                                }
                            },
                            onShare = { listId ->
                                val list = apiLists.find { it.id.toString() == listId }
                                list?.id?.let { id ->
                                    listToShare = Pair(id, list.name)
                                    showShareListDialog = true
                                }
                            },
                            onSearchQueryChange = { query ->
                                shoppingListViewModel.loadShoppingLists(query.ifBlank { null })
                            }
                        )
                    }
                }

                AdaptiveNavigationContainer(
                    isLandscape = showRailNav,
                    paddingValues = innerPadding,
                    navigationRail = if (showRailNav) {
                        {
                            HomeBottomBar(
                                currentDestination = currentDestination,
                                onDestinationSelected = { destination ->
                                    // Clear search when switching tabs
                                    when (destination) {
                                        HomeDestination.Lists -> shoppingListViewModel.loadShoppingLists(null)
                                        HomeDestination.Products -> productViewModel.searchCategoriesAndProducts(null)
                                        HomeDestination.Pantry -> pantryViewModel.searchPantries("")
                                    }
                                    currentDestination = destination
                                },
                                isVertical = true
                            )
                        }
                    } else null,
                    content = renderDestination
                )
            }
        }
        
    // Menu drawer overlay
        MenuDrawer(
            isOpen = isMenuOpen,
            onDismiss = { isMenuOpen = false },
            onLogoutClick = {
                authViewModel.logout()
            },
            userEmail = userEmail,
            userName = currentUser?.name ?: "",
            userSurname = currentUser?.surname ?: "",
            onNameChange = { newName ->
                val currentSurname = currentUser?.surname ?: ""
                if (newName.isNotBlank()) {
                    authViewModel.updateProfile(newName, currentSurname)
                }
            },
            onSurnameChange = { newSurname ->
                val currentName = currentUser?.name ?: ""
                if (currentName.isNotBlank()) {
                    authViewModel.updateProfile(currentName, newSurname)
                }
            },
            onPasswordChangeClick = {
                authViewModel.resetPasswordChangeState()
                showPasswordDialog = true
            },
            isDarkMode = isDarkMode,
            onDarkModeToggle = { isDarkMode = it },
            currentLanguage = currentLanguage,
            onLanguageChange = { newLanguage ->
                currentLanguage = newLanguage
                LocaleHelper.setLocale(context as Activity, newLanguage)
            },
            isSettingsExpanded = LocaleHelper.shouldExpandSettingsOnStart(context)
        )
        
        if (showPasswordDialog) {
            val inlineError = (passwordChangeState as? PasswordChangeState.Error)?.message
            PasswordChangeDialog(
                onDismiss = {
                    if (passwordChangeState !is PasswordChangeState.Loading) {
                        showPasswordDialog = false
                        authViewModel.resetPasswordChangeState()
                    }
                },
                onConfirm = { currentPassword, newPassword ->
                    authViewModel.changePassword(currentPassword, newPassword)
                },
                isProcessing = passwordChangeState is PasswordChangeState.Loading,
                errorMessage = inlineError
            )
        }

        // Create List dialog - creates via API
        if (showCreateListDialog && currentDestination == HomeDestination.Lists) {
            CreateListDialog(
                onDismiss = { showCreateListDialog = false },
                onCreate = { title, leadingIcon ->
                    // Create list via API with icon in metadata
                    val iconName = mapIconToString(leadingIcon)
                    Log.d("MainActivity", "Create clicked: title=$title, icon=$iconName")

                    shoppingListViewModel.createShoppingList(
                        name = title.ifBlank { "New List" },
                        description = "",  // Required by backend
                        recurring = false,  // Required by backend
                        metadata = mapOf("icon" to iconName),
                        onSuccess = { createdList ->
                            showCreateListDialog = false
                            // Show success feedback
                                coroutineScope.launch {
                                    // show localized success message
                                    lastSnackbarIsSuccess = true
                                    val msg = context.getString(R.string.list_created, createdList.name)
                                    snackbarHostState.showSnackbar(
                                        message = msg,
                                        duration = SnackbarDuration.Short)
                                    lastSnackbarIsSuccess = false
                                }
                        }
                    )
                }
            )
        }

        // Create Product dialog
        if (showCreateProductDialog) {
            CreateProductDialog(
                categories = categories,
                onDismiss = { showCreateProductDialog = false },
                onCreate = { name, categoryId ->
                    productViewModel.createProduct(
                        name = name,
                        categoryId = categoryId,
                        metadata = emptyMap(),
                        onSuccess = {
                            showCreateProductDialog = false
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                val msg = context.getString(R.string.product_created, name)
                                snackbarHostState.showSnackbar(
                                    message = msg,
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                }
            )
        }

        // Create Category dialog
        if (showCreateCategoryDialog) {
            CreateCategoryDialog(
                onDismiss = { showCreateCategoryDialog = false },
                onCreate = { name, icon ->
                    val iconName = icon?.let { mapIconToString(it) } ?: "Category"
                    
                    productViewModel.createCategory(
                        name = name,
                        metadata = mapOf("icon" to iconName),
                        onSuccess = {
                            showCreateCategoryDialog = false
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                val msg = context.getString(R.string.category_created, name)
                                snackbarHostState.showSnackbar(
                                    message = msg,
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                }
            )
        }
        
        // Delete Category confirmation dialog
        if (showDeleteCategoryDialog && categoryToDelete != null) {
            val categoryName = categories.find { it.id == categoryToDelete }?.name ?: "this category"
            ConfirmDeleteDialog(
                title = stringResource(id = R.string.delete_category),
                message = stringResource(id = R.string.delete_category_message, categoryName),
                onDismiss = {
                    showDeleteCategoryDialog = false
                    categoryToDelete = null
                },
                onConfirm = {
                    val deletedName = categories.find { it.id == categoryToDelete }?.name ?: "Category"
                    categoryToDelete?.let { id ->
                        productViewModel.deleteCategory(
                            id = id,
                            onSuccess = {
                                showDeleteCategoryDialog = false
                                categoryToDelete = null
                                coroutineScope.launch {
                                    lastSnackbarIsSuccess = false
                                    val msg = context.getString(R.string.category_deleted, deletedName)
                                    snackbarHostState.showSnackbar(
                                        message = msg,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
        
        // Edit Category dialog
        if (showEditCategoryDialog && categoryToEdit != null) {
            val (categoryId, categoryName, categoryIcon) = categoryToEdit!!
            com.example.groceyapp.ui.components.dialogs.EditCategoryDialog(
                currentName = categoryName,
                currentIcon = categoryIcon,
                onDismiss = {
                    showEditCategoryDialog = false
                    categoryToEdit = null
                },
                onUpdate = { newName, newIcon ->
                    val category = categories.find { it.id == categoryId }
                    val iconName = mapIconToString(newIcon)
                    val updatedMetadata = (category?.metadata ?: emptyMap()).toMutableMap().apply {
                        put("icon", iconName)
                    }
                    
                    productViewModel.updateCategory(
                        id = categoryId,
                        name = newName,
                        metadata = updatedMetadata,
                        onSuccess = {
                            showEditCategoryDialog = false
                            categoryToEdit = null
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                val msg = context.getString(R.string.category_updated, newName)
                                snackbarHostState.showSnackbar(
                                    message = msg,
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                }
            )
        }
        
        // Delete List confirmation dialog
        if (showDeleteListDialog && listToDelete != null) {
            val listName = apiLists.find { it.id == listToDelete }?.name ?: "this list"
            ConfirmDeleteDialog(
                title = stringResource(id = R.string.delete_list),
                message = stringResource(id = R.string.delete_list_message, listName),
                onDismiss = {
                    showDeleteListDialog = false
                    listToDelete = null
                },
                onConfirm = {
                    listToDelete?.let { id ->
                        shoppingListViewModel.deleteShoppingList(
                            id = id,
                            onSuccess = {
                                showDeleteListDialog = false
                                // Show success snackbar for deletion
                                val deletedName = apiLists.find { it.id == id }?.name ?: "Lista"
                                coroutineScope.launch {
                                    // deletion should show red (error) snackbar
                                    lastSnackbarIsSuccess = false
                                    val msg = context.getString(R.string.list_deleted, deletedName)
                                    snackbarHostState.showSnackbar(
                                        message = msg,
                                        duration = SnackbarDuration.Short)
                                }
                                listToDelete = null
                            }
                        )
                    }
                }
            )
        }
        
        // Edit List dialog (rename + icon)
        if (showRenameListDialog && listToRename != null) {
            val (listId, listName, listIcon) = listToRename!!
            com.example.groceyapp.ui.components.dialogs.EditListDialog(
                currentName = listName,
                currentIcon = listIcon,
                onDismiss = {
                    showRenameListDialog = false
                    listToRename = null
                },
                onUpdate = { newName, newIcon ->
                    // Map selected ImageVector to metadata key expected by backend
                    val iconName = mapIconToString(newIcon)

                    // Get existing metadata and update icon
                    val existingList = apiLists.find { it.id == listId }
                    val updatedMetadata = (existingList?.metadata?.toMutableMap() ?: mutableMapOf()).apply {
                        this["icon"] = iconName
                    }

                    shoppingListViewModel.updateShoppingList(
                        id = listId,
                        name = newName,
                        metadata = updatedMetadata,
                        onSuccess = {
                            showRenameListDialog = false
                            listToRename = null
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                val msg = context.getString(R.string.list_updated, newName)
                                snackbarHostState.showSnackbar(
                                    message = msg,
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                }
            )
        }
        
        // Share List dialog
        if (showShareListDialog && listToShare != null) {
            val (listId, listName) = listToShare!!
            com.example.groceyapp.ui.components.dialogs.ShareListDialog(
                listName = listName,
                onDismiss = {
                    showShareListDialog = false
                    listToShare = null
                },
                onShare = { emailList ->
                    // Share with each email in the list
                    emailList.forEach { email ->
                        shoppingListViewModel.shareList(
                            listId = listId,
                            email = email,
                            onSuccess = {
                                // Success handled after all shares
                            }
                        )
                    }
                    
                    showShareListDialog = false
                    listToShare = null
                    
                    // Show success message
                    coroutineScope.launch {
                        lastSnackbarIsSuccess = true
                        val msg = context.getString(R.string.list_shared_success)
                        snackbarHostState.showSnackbar(
                            message = msg,
                            duration = SnackbarDuration.Short
                        )
                        lastSnackbarIsSuccess = false
                    }
                }
            )
        }
        
        // Create Pantry dialog
        if (showCreatePantryDialog && currentDestination == HomeDestination.Pantry) {
            CreatePantryDialog(
                onDismiss = { showCreatePantryDialog = false },
                onCreate = { name, icon ->
                    val iconName = mapIconToString(icon)
                    pantryViewModel.createPantry(
                        name = name,
                        metadata = mapOf("icon" to iconName),
                        onSuccess = { createdPantry ->
                            showCreatePantryDialog = false
                            pantryViewModel.loadPantries()
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                val msg = context.getString(R.string.pantry_created, createdPantry.name)
                                snackbarHostState.showSnackbar(
                                    message = msg,
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                }
            )
        }
        
        // Delete Pantry confirmation dialog
        if (showDeletePantryDialog && pantryToDelete != null) {
            val pantryName = pantries.find { it.id == pantryToDelete }?.name ?: "this pantry"
            ConfirmDeleteDialog(
                title = stringResource(id = R.string.delete_pantry),
                message = stringResource(id = R.string.delete_pantry_message, pantryName),
                onDismiss = {
                    showDeletePantryDialog = false
                    pantryToDelete = null
                },
                onConfirm = {
                    val deletedName = pantries.find { it.id == pantryToDelete }?.name ?: "Pantry"
                    pantryToDelete?.let { id ->
                        pantryViewModel.deletePantry(
                            id = id,
                            onSuccess = {
                                showDeletePantryDialog = false
                                pantryToDelete = null
                                coroutineScope.launch {
                                    lastSnackbarIsSuccess = false
                                    val msg = context.getString(R.string.pantry_deleted, deletedName)
                                    snackbarHostState.showSnackbar(
                                        message = msg,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
        
        // Edit Pantry dialog
        if (showEditPantryDialog && pantryToEdit != null) {
            val (pantryId, pantryName, pantryIcon) = pantryToEdit!!
            EditPantryDialog(
                currentName = pantryName,
                currentIcon = pantryIcon,
                onDismiss = {
                    showEditPantryDialog = false
                    pantryToEdit = null
                },
                onUpdate = { newName, newIcon ->
                    val pantry = pantries.find { it.id == pantryId }
                    val iconName = mapIconToString(newIcon)
                    val updatedMetadata = (pantry?.metadata ?: emptyMap()).toMutableMap().apply {
                        put("icon", iconName)
                    }
                    
                    pantryViewModel.updatePantry(
                        id = pantryId,
                        name = newName,
                        metadata = updatedMetadata,
                        onSuccess = {
                            showEditPantryDialog = false
                            pantryToEdit = null
                            coroutineScope.launch {
                                lastSnackbarIsSuccess = true
                                val msg = context.getString(R.string.pantry_updated, newName)
                                snackbarHostState.showSnackbar(
                                    message = msg,
                                    duration = SnackbarDuration.Short
                                )
                                lastSnackbarIsSuccess = false
                            }
                        }
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListsPreview() {
    GroceyAppTheme {
        ListsApp()
    }
}
}
