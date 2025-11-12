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
import com.example.groceyapp.ui.components.PrimaryFab
import com.example.groceyapp.ui.lists.HomeBottomBar
import com.example.groceyapp.ui.lists.HomeDestination
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
            HomeDestination.Lists -> ListsScreen(modifier = contentModifier)
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
