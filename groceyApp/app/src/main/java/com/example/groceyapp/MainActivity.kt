package com.example.groceyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceyapp.R
import com.example.groceyapp.ui.auth.AuthenticationScreen
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.MenuDrawer
import com.example.groceyapp.ui.components.PrimaryFab
import com.example.groceyapp.ui.lists.defaultListItemsPublic
import com.example.groceyapp.ui.lists.HomeBottomBar
import com.example.groceyapp.ui.lists.HomeDestination
import com.example.groceyapp.ui.lists.ListDetailScreen
import com.example.groceyapp.ui.lists.ListsScreen
import com.example.groceyapp.ui.lists.PantryScreen
import com.example.groceyapp.ui.lists.ProductsScreen
import com.example.groceyapp.ui.theme.GroceyAppTheme
import com.example.groceyapp.ui.viewmodel.AuthViewModel

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
    // Get AuthViewModel instance
    val authViewModel: AuthViewModel = viewModel()
    
    // Collect authentication state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var currentDestination by remember { mutableStateOf(HomeDestination.Lists) }
    var selectedListId by remember { mutableStateOf<String?>(null) }
    var isMenuOpen by remember { mutableStateOf(false) }
    
    // Settings state
    var isDarkMode by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("en") }
    
    // Mock data - would come from a ViewModel/Repository in a real app
    // API CHANGE: Replace `defaultListItemsPublic()` with a ViewModel-provided state (e.g.
    // `val mockLists by collectionsViewModel.listsState.collectAsState()`) that reads from
    // the Repository/DB or remote API instead of hard-coded sample data.
    val mockLists: List<ListCardData> = defaultListItemsPublic()

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
    val selectedList: ListCardData? = selectedListId?.let { id: String ->
        mockLists.find { list -> list.id == id }
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
                    val (labelRes, action) = when (currentDestination) {
                        HomeDestination.Pantry -> R.string.add_pantry_item to { /* TODO: pantry add action */ }
                        HomeDestination.Products -> R.string.add_product to { /* TODO: product add action */ }
                        HomeDestination.Lists -> R.string.add_list to { /* TODO: list add action */ }
                    }
                    PrimaryFab(contentDescriptionRes = labelRes, onClick = action)
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
                        onMenuClick = { isMenuOpen = true }
                    )
                    HomeDestination.Lists -> ListsScreen(
                        modifier = contentModifier,
                        items = mockLists,
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
    }
}

@Preview(showBackground = true)
@Composable
fun ListsPreview() {
    GroceyAppTheme {
        ListsApp()
    }
}
