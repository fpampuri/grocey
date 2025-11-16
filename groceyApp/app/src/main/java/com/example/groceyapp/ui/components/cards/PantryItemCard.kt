package com.example.groceyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.data.model.Pantry

/**
 * Data class representing a pantry item within a pantry
 */
data class PantryItemCardData(
    val id: Int,
    val productName: String,
    val categoryName: String,
    val quantity: Double
)

/**
 * Card for displaying a pantry item with quantity controls
 * Shows: product name, category as description, quantity with +/- buttons, and a menu
 */
@Composable
fun PantryItemCard(
    data: PantryItemCardData,
    pantries: List<Pantry> = emptyList(),
    onQuantityChange: (Double) -> Unit = {},
    onMoveToPantry: (Int) -> Unit = {},
    onDeleteItem: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Keep a transient string state for smooth typing
    var qtyText by remember(data.quantity) { mutableStateOf(
        if (data.quantity % 1.0 == 0.0) {
            data.quantity.toInt().toString()
        } else {
            String.format("%.1f", data.quantity)
        }
    ) }
    
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
            // Product name and category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.productName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = data.categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Minus button
                IconButton(
                    onClick = { 
                        if (data.quantity > 0.5) {
                            onQuantityChange(data.quantity - 1.0)
                        }
                    },
                    enabled = data.quantity > 0.5,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        tint = if (data.quantity > 0.5) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Editable quantity field
                TextField(
                    value = qtyText,
                    onValueChange = { newText ->
                        // Allow digits and one decimal point
                        val filtered = newText.filter { it.isDigit() || it == '.' }
                        // Ensure only one decimal point
                        if (filtered.count { it == '.' } <= 1) {
                            qtyText = filtered
                            val parsed = filtered.toDoubleOrNull()
                            if (parsed != null && parsed > 0) {
                                onQuantityChange(parsed)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.width(72.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )

                // Plus button
                IconButton(
                    onClick = { onQuantityChange(data.quantity + 1.0) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Three-dot menu
            com.example.groceyapp.ui.components.general.PantryItemOptionsMenu(
                pantries = pantries,
                onMoveToPantry = onMoveToPantry,
                onDelete = onDeleteItem,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
