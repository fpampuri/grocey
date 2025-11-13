package com.example.groceyapp.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.data.model.Category
import com.example.groceyapp.ui.components.ButtonVariant
import com.example.groceyapp.ui.components.StandardButton

/**
 * Dialog for creating a new product
 */
@Composable
fun CreateProductDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onCreate: (name: String, categoryId: Int?) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.add_product))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text(stringResource(id = R.string.product_name_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category selector
                OutlinedTextField(
                    value = selectedCategory?.name ?: stringResource(id = R.string.no_category),
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.select_category)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { expanded = true }
                        )
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.no_category)) },
                        onClick = {
                            selectedCategory = null
                            expanded = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                StandardButton(
                    title = stringResource(id = android.R.string.cancel),
                    onClick = onDismiss,
                    icon = Icons.Filled.Close,
                    variant = ButtonVariant.DANGER
                )
                Spacer(modifier = Modifier.width(8.dp))
                StandardButton(
                    title = stringResource(id = R.string.create),
                    onClick = {
                        onCreate(productName.trim(), selectedCategory?.id)
                    },
                    icon = Icons.Filled.Add,
                    enabled = productName.isNotBlank()
                )
            }
        },
        dismissButton = null
    )
}
