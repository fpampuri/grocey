package com.example.groceyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.ListCardData
import com.example.groceyapp.ui.components.PrimaryFab
import com.example.groceyapp.ui.lists.defaultListItemsPublic
import com.example.groceyapp.ui.lists.HomeBottomBar
import com.example.groceyapp.ui.lists.HomeDestination
import com.example.groceyapp.ui.lists.ListDetailScreen
import com.example.groceyapp.ui.lists.ListsScreen
import com.example.groceyapp.ui.lists.PantryScreen
import com.example.groceyapp.ui.lists.ProductsScreen
import com.example.groceyapp.ui.theme.GroceyAppTheme

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
    var currentDestination by remember { mutableStateOf(HomeDestination.Lists) }
    var selectedListId by remember { mutableStateOf<String?>(null) }
    
    // Mock data - would come from a ViewModel in a real app
    val mockLists: List<ListCardData> = defaultListItemsPublic()

    // Determine if we're showing list detail
    val selectedList: ListCardData? = selectedListId?.let { id: String ->
        mockLists.find { list -> list.id == id }
    }

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
            }
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
                HomeDestination.Pantry -> PantryScreen(modifier = contentModifier)
                HomeDestination.Products -> ProductsScreen(modifier = contentModifier)
                HomeDestination.Lists -> ListsScreen(
                    modifier = contentModifier,
                    items = mockLists,
                    onListClick = { listId -> selectedListId = listId }
                )
            }
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
