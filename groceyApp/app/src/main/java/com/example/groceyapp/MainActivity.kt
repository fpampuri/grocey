package com.example.groceyapp

import android.os.Bundle
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
import com.example.groceyapp.R
import com.example.groceyapp.ui.auth.AuthenticationScreen
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.general.MenuDrawer
import com.example.groceyapp.ui.components.general.PrimaryFab
import com.example.groceyapp.ui.components.dialogs.CreateListDialog
import com.example.groceyapp.ui.screens.HomeBottomBar
import com.example.groceyapp.ui.screens.HomeDestination
import com.example.groceyapp.ui.screens.ListDetailScreen
import com.example.groceyapp.ui.screens.ListsScreen
import com.example.groceyapp.ui.screens.PantryScreen
import com.example.groceyapp.ui.screens.ProductsScreen
import com.example.groceyapp.ui.components.dialogs.CreateCategoryDialog
import com.example.groceyapp.ui.components.dialogs.CreateProductDialog
import com.example.groceyapp.ui.components.dialogs.ConfirmDeleteDialog
import com.example.groceyapp.ui.components.dialogs.RenameCategoryDialog
import com.example.groceyapp.ui.theme.GroceyAppTheme
import com.example.groceyapp.ui.viewmodel.AuthViewModel
import com.example.groceyapp.ui.viewmodel.ProductViewModel
import com.example.groceyapp.ui.viewmodel.ShoppingListViewModel
import androidx.compose.runtime.LaunchedEffect

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
    var isMenuOpen by remember { mutableStateOf(false) }
    
    // Settings state
    var isDarkMode by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("en") }
    
    // Shopping lists from API
    val apiLists by shoppingListViewModel.lists.collectAsState()
    var showCreateListDialog by remember { mutableStateOf(false) }
    
    // Products and categories dialog states
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var showProductFabMenu by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var showRenameCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Int?>(null) }
    var categoryToRename by remember { mutableStateOf<Pair<Int, String>?>(null) }
    
    // Products and categories from API
    val products by productViewModel.products.collectAsState()
    val categories by productViewModel.categories.collectAsState()
    
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
        val icon = when (iconName) {
            "store" -> Icons.Filled.Store
            "inventory" -> Icons.Filled.Inventory2
            else -> Icons.Rounded.ShoppingCart
        }
        
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
        // Extract icon from metadata
        val iconName = category.metadata?.get("icon") as? String
        val icon = when (iconName) {
            "ShoppingCart" -> Icons.Rounded.ShoppingCart
            "store" -> Icons.Filled.Store
            "inventory" -> Icons.Filled.Inventory2
            "List" -> Icons.AutoMirrored.Filled.List
            "Star" -> Icons.Filled.Star
            "StarBorder" -> Icons.Outlined.StarBorder
            "Add" -> Icons.Filled.Add
            "Remove" -> Icons.Filled.Remove
            "MoreVert" -> Icons.Filled.MoreVert
            "Delete" -> Icons.Filled.Delete
            "Edit" -> Icons.Filled.Edit
            "Share" -> Icons.Filled.Share
            "FilterList" -> Icons.Filled.FilterList
            "Search" -> Icons.Filled.Search
            "ChevronRight" -> Icons.Filled.ChevronRight
            "Circle" -> Icons.Filled.Circle
            "Person" -> Icons.Filled.Person
            "Email" -> Icons.Filled.Email
            "Lock" -> Icons.Filled.Lock
            "DarkMode" -> Icons.Filled.DarkMode
            "Language" -> Icons.Filled.Language
            "Visibility" -> Icons.Filled.Visibility
            "VisibilityOff" -> Icons.Filled.VisibilityOff
            "ArrowBack" -> Icons.AutoMirrored.Filled.ArrowBack
            "Home" -> Icons.Filled.Home
            "Bookmark" -> Icons.Filled.Bookmark
            "LocalOffer" -> Icons.Filled.LocalOffer
            "Tag" -> Icons.Filled.Tag
            "LocalGroceryStore" -> Icons.Rounded.LocalGroceryStore
            "Favorite" -> Icons.Filled.Favorite
            "FavoriteBorder" -> Icons.Filled.FavoriteBorder
            "ShoppingBasket" -> Icons.Filled.ShoppingBasket
            "Restaurant" -> Icons.Filled.Restaurant
            "Fastfood" -> Icons.Filled.Fastfood
            "LocalDining" -> Icons.Filled.LocalDining
            else -> Icons.Filled.Category
        }
        
        // Get products for this category
        val categoryProducts = products
            .filter { it.category?.id == category.id }
            .map { it.name }
        
        com.example.groceyapp.ui.components.CategoryCardData(
            title = category.name,
            subtitle = "${categoryProducts.size} products",
            leadingIcon = icon,
            products = categoryProducts
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
            // Show detail screen
            ListDetailScreen(
                listData = selectedList,
                onBackClick = { selectedListId = null },
                onProductToggle = { productId ->
                    // TODO: Handle product toggle
                },
                onQuantityChange = { productId, newQuantity ->
                    // TODO: Handle quantity change
                },
                currentDestination = currentDestination,
                onDestinationSelected = { destination ->
                    currentDestination = destination
                    selectedListId = null  // Go back to main view when switching tabs
                },
                onMenuClick = { isMenuOpen = true }
            )
        } else {
            // Show main app with bottom navigation
            Scaffold(
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
                            categoryId?.let {
                                val category = categories.find { cat -> cat.id == it.toInt() }
                                category?.let { cat ->
                                    cat.id?.let { id ->
                                        categoryToRename = Pair(id, cat.name)
                                        showRenameCategoryDialog = true
                                    }
                                }
                            }
                        }
                    )
                    HomeDestination.Lists -> ListsScreen(
                        modifier = contentModifier,
                        items = lists,
                        onListClick = { listId -> selectedListId = listId },
                        onMenuClick = { isMenuOpen = true }
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
                    val iconName = when (leadingIcon) {
                        Icons.Rounded.ShoppingCart -> "shopping_cart"
                        Icons.Filled.Store -> "store"
                        Icons.Filled.Inventory2 -> "inventory"
                        else -> "list"
                    }
                    
                    shoppingListViewModel.createShoppingList(
                        name = title.ifBlank { "New List" },
                        description = "",  // Required by backend
                        recurring = false,  // Required by backend
                        metadata = mapOf("icon" to iconName),
                        onSuccess = {
                            showCreateListDialog = false
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
                    // Map icon to simple name for storage
                    val iconName = when (icon) {
                        Icons.Rounded.ShoppingCart -> "ShoppingCart"
                        Icons.Filled.Store -> "store"
                        Icons.Filled.Inventory2 -> "inventory"
                        Icons.AutoMirrored.Filled.List -> "List"
                        Icons.Filled.Star -> "Star"
                        Icons.Outlined.StarBorder -> "StarBorder"
                        Icons.Filled.Add -> "Add"
                        Icons.Filled.Remove -> "Remove"
                        Icons.Filled.MoreVert -> "MoreVert"
                        Icons.Filled.Delete -> "Delete"
                        Icons.Filled.Edit -> "Edit"
                        Icons.Filled.Share -> "Share"
                        Icons.Filled.FilterList -> "FilterList"
                        Icons.Filled.Search -> "Search"
                        Icons.Filled.ChevronRight -> "ChevronRight"
                        Icons.Filled.Circle -> "Circle"
                        Icons.Filled.Person -> "Person"
                        Icons.Filled.Email -> "Email"
                        Icons.Filled.Lock -> "Lock"
                        Icons.Filled.DarkMode -> "DarkMode"
                        Icons.Filled.Language -> "Language"
                        Icons.Filled.Visibility -> "Visibility"
                        Icons.Filled.VisibilityOff -> "VisibilityOff"
                        Icons.AutoMirrored.Filled.ArrowBack -> "ArrowBack"
                        Icons.Filled.Home -> "Home"
                        Icons.Filled.Bookmark -> "Bookmark"
                        Icons.Filled.LocalOffer -> "LocalOffer"
                        Icons.Filled.Tag -> "Tag"
                        Icons.Rounded.LocalGroceryStore -> "LocalGroceryStore"
                        Icons.Filled.Favorite -> "Favorite"
                        Icons.Filled.FavoriteBorder -> "FavoriteBorder"
                        Icons.Filled.ShoppingBasket -> "ShoppingBasket"
                        Icons.Filled.Restaurant -> "Restaurant"
                        Icons.Filled.Fastfood -> "Fastfood"
                        Icons.Filled.LocalDining -> "LocalDining"
                        else -> "category"
                    }
                    
                    productViewModel.createCategory(
                        name = name,
                        metadata = mapOf("icon" to iconName),
                        onSuccess = {
                            showCreateCategoryDialog = false
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
                    categoryToDelete?.let { id ->
                        productViewModel.deleteCategory(
                            id = id,
                            onSuccess = {
                                showDeleteCategoryDialog = false
                                categoryToDelete = null
                            }
                        )
                    }
                }
            )
        }
        
        // Rename Category dialog
        if (showRenameCategoryDialog && categoryToRename != null) {
            val (categoryId, categoryName) = categoryToRename!!
            RenameCategoryDialog(
                currentName = categoryName,
                onDismiss = {
                    showRenameCategoryDialog = false
                    categoryToRename = null
                },
                onRename = { newName ->
                    val category = categories.find { it.id == categoryId }
                    productViewModel.updateCategory(
                        id = categoryId,
                        name = newName,
                        metadata = category?.metadata,
                        onSuccess = {
                            showRenameCategoryDialog = false
                            categoryToRename = null
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
