package com.example.groceyapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.groceyapp.Constants
// Use semantic roles from MaterialTheme.colorScheme

data class CategoryCardData(
    val id: Long? = null,
    val title: String,
    val subtitle: String,
    val leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeText: String? = null,
    val products: List<String> = emptyList()
)

@Composable
fun CategoryCard(
    data: CategoryCardData,
    modifier: Modifier = Modifier,
    onDelete: (Long?) -> Unit = {},
    onRename: (Long?) -> Unit = {},
    onClick: (CategoryCardData) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val isMiscellaneous = data.id == Constants.MISCELLANEOUS_CATEGORY_ID

    CollectionCardShell(
        onClick = { onClick(data) },
        modifier = modifier
    ) {
        // Main card row (content placed directly into the Row provided by the shell)
        Icon(
            imageVector = data.leadingIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
            Text(
                text = data.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // data.badgeText?.let { badge ->
        //     SharedBadge(badge)
        //     Spacer(modifier = Modifier.width(8.dp))
        // }

        // Chevron icon
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "View details",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Three-dot menu (only show if not Miscellaneous)
        if (!isMiscellaneous) {
            IconButton(
                onClick = { 
                    showMenu = !showMenu
                },
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
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        onDelete(data.id)
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
    }
}


