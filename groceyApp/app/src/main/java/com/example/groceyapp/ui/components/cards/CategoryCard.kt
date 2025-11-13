package com.example.groceyapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import com.example.groceyapp.ui.theme.BrandGreenLight
import com.example.groceyapp.ui.theme.BrandGreenLightDarkTheme

data class CategoryCardData(
    val title: String,
    val subtitle: String,
    val leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeText: String? = null,
    val products: List<String> = emptyList()
)

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

    CollectionCardShell(
        onClick = { isExpanded = !isExpanded },
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

        androidx.compose.foundation.layout.Column(
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

