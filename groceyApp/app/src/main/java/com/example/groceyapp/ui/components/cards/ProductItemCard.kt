package com.example.groceyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R

/**
 * Mode for displaying the product item card
 */
enum class ProductCardMode {
    LIST,      // In a shopping list: shows checkbox and quantity controls
    CATEGORY   // In a category view: shows only name and 3-dot menu
}

/**
 * Data class representing a product item within a list
 */
data class ProductItemData(
    val id: String,
    val name: String,
    val category: String,
    val quantity: Int = 1,
    val isBought: Boolean = false
)

/**
 * Product item card - Displays individual products within a list
 * Features: Checkbox (mark as bought), name, category, quantity controls
 * Can be displayed in two modes: LIST (with checkbox/quantity) or CATEGORY (with menu)
 */
@Composable
fun ProductItemCard(
    data: ProductItemData,
    onToggleBought: () -> Unit = {},
    onQuantityChange: (Int) -> Unit = {},
    mode: ProductCardMode = ProductCardMode.LIST,
    onMoveToCategory: () -> Unit = {},
    onAddToList: () -> Unit = {},
    onAddToPantry: () -> Unit = {},
    onDeleteProduct: () -> Unit = {},
    onDeleteItem: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for marking as bought (only in LIST mode)
            if (mode == ProductCardMode.LIST) {
                Checkbox(
                    checked = data.isBought,
                    onCheckedChange = { onToggleBought() }
                )
                // API CHANGE: This action should call a ViewModel/Repository method to persist
                // the bought state (e.g. `listDetailViewModel.toggleBought(productId)`);
                // ensure callers pass a function that performs persistence instead of only
                // mutating in-memory UI state.

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Product name and category
            androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = if (mode == ProductCardMode.LIST && data.isBought)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    // Strike-through when bought (only in LIST mode)
                    textDecoration = if (mode == ProductCardMode.LIST && data.isBought) TextDecoration.LineThrough else TextDecoration.None
                )
                // Only show category in LIST mode
                if (mode == ProductCardMode.LIST) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Quantity controls (only in LIST mode)
            if (mode == ProductCardMode.LIST) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Minus button
                    IconButton(
                        onClick = { 
                            if (data.quantity > 1) {
                                onQuantityChange(data.quantity - 1)
                            }
                        },
                        enabled = data.quantity > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.decrease_quantity),
                            tint = if (data.quantity > 1) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    // API CHANGE: Quantity updates should be persisted via ViewModel/Repository
                    // (e.g. `listDetailViewModel.setQuantity(productId, newQuantity)`).

                    // Editable quantity field (small). We keep a transient string state here so
                    // typing is smooth; final value is emitted via `onQuantityChange`.
                    var qtyText by remember { mutableStateOf(data.quantity.toString()) }
                    // Keep the local text in sync when the data.quantity from parent changes
                    LaunchedEffect(data.quantity) { qtyText = data.quantity.toString() }

                    TextField(
                        value = qtyText,
                        onValueChange = { newText ->
                            // Allow only digits (empty allowed while editing)
                            val filtered = newText.filter { it.isDigit() }
                            qtyText = filtered
                            val parsed = filtered.toIntOrNull()
                            if (parsed != null) {
                                onQuantityChange(parsed)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.width(72.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    )

                    // Plus button
                    IconButton(
                        onClick = { onQuantityChange(data.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.increase_quantity),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Trash button (optional) to remove from list
                    if (onDeleteItem != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onDeleteItem, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = androidx.compose.ui.res.stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            } else {
                // Three-dot menu (only in CATEGORY mode)
                ProductOptionsMenu(
                    onMoveToCategory = onMoveToCategory,
                    onAddToList = onAddToList,
                    onAddToPantry = onAddToPantry,
                    onDelete = onDeleteProduct,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
