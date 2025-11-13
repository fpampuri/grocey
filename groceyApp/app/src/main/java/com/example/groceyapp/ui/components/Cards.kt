package com.example.groceyapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R
import com.example.groceyapp.ui.theme.BrandGreenLight
import com.example.groceyapp.ui.theme.BrandGreenLightDarkTheme

/**
 * Data class representing a list item with extended functionality
 */
data class ListCardData(
    val id: String,
    val title: String,
    val itemCount: Int,
    val leadingIcon: ImageVector,
    val isFavorite: Boolean = false,
    val isShared: Boolean = false,
    val products: List<ProductItemData> = emptyList()
)

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
 * Data class representing a category card (for Pantry/Products screens)
 */
data class CategoryCardData(
    val title: String,
    val subtitle: String,
    val leadingIcon: ImageVector,
    val badgeText: String? = null,
    val products: List<String> = emptyList()
)

/**
 * List card component - Larger card designed specifically for shopping lists
 * Features: Icon, name, favorite toggle, three-dot menu, navigates to detail screen
 */
@Composable
fun ListCard(
    data: ListCardData,
    onClick: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {},
    onRename: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SolidColor(borderColor)),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // Main card header - larger than category cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(data.id) }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon - larger for lists
            Icon(
                imageVector = data.leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            
            // Title and item count
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.items_count, data.itemCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Shared badge if applicable
            if (data.isShared) {
                SharedBadge(stringResource(R.string.shared_label))
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Favorite star toggle
            // API CHANGE: The favorite toggle should persist via ViewModel/Repository.
            // Callers should pass a ViewModel method (e.g. `collectionsViewModel.toggleFavorite(id)`) 
            // that updates the backend/DB instead of mutating only in-memory state.
            IconButton(
                onClick = { 
                    onFavoriteToggle(data.id)
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (data.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (data.isFavorite) 
                        stringResource(R.string.remove_from_favorites) 
                    else 
                        stringResource(R.string.add_to_favorites),
                    tint = if (data.isFavorite) 
                        MaterialTheme.colorScheme.secondary 
                    else 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Three-dot menu
            Box {
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    // API CHANGE: Renaming/sharing/deleting a list should call into a ViewModel
                    // method that updates the repository (remote/local DB). Ensure the
                    // implementation performs the persistence operation and emits new state
                    // observable for the UI to consume.
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.rename_list)) },
                        onClick = {
                            showMenu = false
                            onRename(data.id)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.share_list)) },
                        onClick = {
                            showMenu = false
                            onShare(data.id)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete_list)) },
                        onClick = {
                            showMenu = false
                            onDelete(data.id)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Product item card - Displays individual products within a list
 * Features: Checkbox (mark as bought), name, category, quantity controls
 */
@Composable
fun ProductItemCard(
    data: ProductItemData,
    onToggleBought: () -> Unit = {},
    onQuantityChange: (Int) -> Unit = {},
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
            // Checkbox for marking as bought
            Checkbox(
                checked = data.isBought,
                onCheckedChange = { onToggleBought() }
            )
                // API CHANGE: This action should call a ViewModel/Repository method to persist
                // the bought state (e.g. `listDetailViewModel.toggleBought(productId)`);
                // ensure callers pass a function that performs persistence instead of only
                // mutating in-memory UI state.
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Product name and category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = if (data.isBought) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = data.category,
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
                        if (data.quantity > 1) {
                            onQuantityChange(data.quantity - 1)
                        }
                    },
                    enabled = data.quantity > 1,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.decrease_quantity),
                        tint = if (data.quantity > 1) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                    // API CHANGE: Quantity updates should be persisted via ViewModel/Repository
                    // (e.g. `listDetailViewModel.setQuantity(productId, newQuantity)`).
                
                // Quantity display
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Plus button
                IconButton(
                    onClick = { onQuantityChange(data.quantity + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.increase_quantity),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Category card component - Used for Pantry and Products screens
 * Kept as is per user requirements - simpler design with expandable product list
 */
@Composable
fun CategoryCard(
    data: CategoryCardData,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "chevron rotation"
    )

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SolidColor(borderColor)),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Main card row (clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = data.leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = data.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                data.badgeText?.let { badge ->
                    SharedBadge(badge)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Chevron icon (rotates when expanded)
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.rotate(rotationAngle)
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Three-dot menu
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = {
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Expanded content with products list
            if (isExpanded && data.products.isNotEmpty()) {
                val lightGreenBg = if (isSystemInDarkTheme()) {
                    BrandGreenLightDarkTheme.copy(alpha = 0.15f)
                } else {
                    BrandGreenLight.copy(alpha = 0.15f)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGreenBg)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                ) {
                    data.products.forEach { product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = product,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Shared badge component for indicating shared status
 */
@Composable
private fun SharedBadge(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
