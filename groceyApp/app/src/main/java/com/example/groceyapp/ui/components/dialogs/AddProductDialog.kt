package com.example.groceyapp.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.data.model.Category
import com.example.groceyapp.data.model.Product

/**
 * Dialog for adding a product to a shopping list.
 * Provides two modes:
 * 1. Type a custom product name manually
 * 2. Select a category (for future product selection from that category)
 *
 * @param onDismiss Callback when dialog is dismissed
 * @param onProductSelected Callback when a product is added (productName, categoryId)
 * @param categories List of available categories to associate products with
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onProductSelected: (String, Int?) -> Unit, // (productName, categoryId)
    categories: List<Category>
) {
    var customProductName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }

    val selectedCategory = categories.find { it.id == selectedCategoryId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.add_product_title))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Option 1: Type custom product name
                OutlinedTextField(
                    value = customProductName,
                    onValueChange = { 
                        customProductName = it
                    },
                    label = { Text(stringResource(R.string.product_name_label)) },
                    placeholder = { Text(stringResource(R.string.type_product_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category dropdown (optional)
                Text(
                    text = stringResource(R.string.select_category),
                    style = MaterialTheme.typography.bodyMedium
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: stringResource(R.string.no_category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (customProductName.isNotEmpty()) {
                        onProductSelected(customProductName, selectedCategoryId)
                        onDismiss()
                    }
                },
                enabled = customProductName.isNotEmpty()
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
