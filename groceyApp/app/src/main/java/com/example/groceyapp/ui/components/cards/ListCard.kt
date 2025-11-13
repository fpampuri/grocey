package com.example.groceyapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R

/**
 * Data class representing a list item with extended functionality
 */
data class ListCardData(
    val id: String,
    val title: String,
    val itemCount: Int,
    val leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val isFavorite: Boolean = false,
    val isShared: Boolean = false,
    val products: List<ProductItemData> = emptyList()
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
                    text = androidx.compose.ui.res.stringResource(R.string.items_count, data.itemCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Shared badge if applicable
            if (data.isShared) {
                SharedBadge(androidx.compose.ui.res.stringResource(R.string.shared_label))
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
                        androidx.compose.ui.res.stringResource(R.string.remove_from_favorites) 
                    else 
                        androidx.compose.ui.res.stringResource(R.string.add_to_favorites),
                    tint = if (data.isFavorite) 
                        MaterialTheme.colorScheme.secondary 
                    else 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Three-dot menu
            androidx.compose.foundation.layout.Box {
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = androidx.compose.ui.res.stringResource(R.string.more_options),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
                
                androidx.compose.material3.DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    // API CHANGE: Renaming/sharing/deleting a list should call into a ViewModel
                    // method that updates the repository (remote/local DB). Ensure the
                    // implementation performs the persistence operation and emits new state
                    // observable for the UI to consume.
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(androidx.compose.ui.res.stringResource(R.string.rename_list)) },
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
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(androidx.compose.ui.res.stringResource(R.string.share_list)) },
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
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(androidx.compose.ui.res.stringResource(R.string.delete_list)) },
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
