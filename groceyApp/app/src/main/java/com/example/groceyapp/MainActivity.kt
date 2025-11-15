package com.example.groceyapp

import android.os.Bundle
import androidx.compose.material3.SnackbarDuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.example.groceyapp.ui.components.dialogs.CreateListDialog
import com.example.groceyapp.ui.screens.HomeBottomBar
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
import com.example.groceyapp.ui.theme.GroceyAppTheme
import com.example.groceyapp.ui.viewmodel.AuthViewModel
import com.example.groceyapp.ui.viewmodel.ProductViewModel
import com.example.groceyapp.ui.viewmodel.ShoppingListViewModel
import com.example.groceyapp.ui.utils.mapIconToString
import com.example.groceyapp.ui.utils.mapStringToIcon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.MaterialTheme
import android.util.Log
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceyAppTheme {
                ListsApp()
            }
        }
    }
}

@Composable
fun ListsApp() {
    // Get ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val shoppingListViewModel: ShoppingListViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    
    // Collect authentication state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Restore session when app starts (only runs once)
    LaunchedEffect(Unit) {
        authViewModel.restoreSession()
    }
    
    var currentDestination by remember { mutableStateOf(HomeDestination.Lists) }
    var selectedListId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryCardData?>(null) }
    var isMenuOpen by remember { mutableStateOf(false) }
    
    // Settings state
    var isDarkMode by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("en") }
    
    // Shopping lists from API
    val apiLists by shoppingListViewModel.lists.collectAsState()
    var showCreateListDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }
    var showRenameListDialog by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<Int?>(null) }
    var listToRename by remember { mutableStateOf<Triple<Int, String, androidx.compose.ui.graphics.vector.ImageVector?>?>(null) }
    
    // Products and categories dialog states
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var showProductFabMenu by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Int?>(null) }
    var categoryToEdit by remember { mutableStateOf<Triple<Int, String, androidx.compose.ui.graphics.vector.ImageVector?>?>(null) }
    
    // Products and categories from API
    val products by productViewModel.products.collectAsState()
    val categories by productViewModel.categories.collectAsState()

    val context = LocalContext.current

    // Snackbar + coroutine scope for showing feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var lastSnackbarIsSuccess by remember { mutableStateOf(false) }
    val errorMessage by shoppingListViewModel.errorMessage.collectAsState()

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
            itemCount = 0, // Will be populated when we load items
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

    // Show authentication screen if not authenticated
    if (!isAuthenticated) {
        AuthenticationScreen(
            onLoginSuccess = {
                // Authentication handled by viewModel
                // User will be automatically authenticated when login succeeds
            }
        )
        return
    }
    
    // Extract user info from currentUser
    val userEmail = currentUser?.email ?: "user@gmail.com"
    val userName = "${currentUser?.name ?: "User"} ${currentUser?.surname ?: ""}"

    // Determine if we're showing list detail
    val selectedList: ListCardData? = selectedListId?.let { id ->
        lists.find { list -> list.id == id }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedList != null) {
            // Show list detail screen
                ListDetailScreen(
                    listData = selectedList,
                    onBackClick = { selectedListId = null },
                    onProductToggle = { _ ->
                        // TODO: Handle product toggle
                    },
                    onQuantityChange = { _, _ ->
                        // TODO: Handle quantity change
                    },
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
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
                    onShare = { _ -> /* share intentionally disabled until specified */ }
                )
        } else if (selectedCategory != null) {
            // Show category detail screen
            val category = selectedCategory!!
            CategoryDetailScreen(
                categoryData = category,
                products = products,
                categories = categories,
                onBackClick = { selectedCategory = null },
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
                onProductAddToList = { productId ->
                    // TODO: Handle add to list
                },
                onProductAddToPantry = { productId ->
                    // TODO: Handle add to pantry
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
                                    message = "Product deleted",
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
                                    message = "Product created successfully",
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
                    currentDestination = destination
                    selectedCategory = null  // Go back to main view when switching tabs
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
                    when (currentDestination) {
                        HomeDestination.Pantry -> {
                            PrimaryFab(
                                contentDescriptionRes = R.string.add_pantry_item,
                                onClick = { /* TODO: pantry add action */ }
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
                },
                bottomBar = {
                    HomeBottomBar(
                        currentDestination = currentDestination,
                        onDestinationSelected = { currentDestination = it }
                    )
                }
            ) { innerPadding ->
                val contentModifier = Modifier.padding(innerPadding)
                when (currentDestination) {
                    HomeDestination.Pantry -> PantryScreen(
                        modifier = contentModifier,
                        onMenuClick = { isMenuOpen = true }
                    )
                    HomeDestination.Products -> ProductsScreen(
                        modifier = contentModifier,
                        items = categoryCards,
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
                            selectedCategory = categoryData
                        }
                    )
                    HomeDestination.Lists -> ListsScreen(
                        modifier = contentModifier,
                        items = lists,
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
                        }
                    )
                }
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
            userName = userName,
            onNameChange = { newName ->
                // Parse name into first and last name
                val nameParts = newName.trim().split(" ", limit = 2)
                val firstName = nameParts.getOrNull(0) ?: ""
                val lastName = nameParts.getOrNull(1) ?: ""
                
                if (firstName.isNotBlank()) {
                    authViewModel.updateProfile(firstName, lastName)
                }
            },
            isDarkMode = isDarkMode,
            onDarkModeToggle = { isDarkMode = it },
            currentLanguage = currentLanguage,
            onLanguageChange = { newLanguage ->
                currentLanguage = newLanguage
                // TODO: Implement actual language change logic
                // This would typically involve changing the app's locale
            }
        )

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
    }
}

@Preview(showBackground = true)
@Composable
fun ListsPreview() {
    GroceyAppTheme {
        ListsApp()
    }
}
