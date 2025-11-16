package com.example.groceyapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.groceyapp.R
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Use semantic roles from MaterialTheme.colorScheme

data class CategoryCardData(
    val id: Long? = null,
    val title: String,
    val subtitle: String,
    val leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeText: String? = null,
    val products: List<String> = emptyList(),
    val isProtected: Boolean = false
)

@Composable
fun CategoryCard(
    data: CategoryCardData,
    onDelete: (Long?) -> Unit = {},
    onRename: (Long?) -> Unit = {},
    onClick: (CategoryCardData) -> Unit = {},
    editTextRes: Int? = null,
    deleteTextRes: Int? = null,
    modifier: Modifier = Modifier
) {
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

        // Three-dot menu using shared component
        com.example.groceyapp.ui.components.general.CategoryOptionsMenu(
            categoryId = data.id,
            isProtected = data.isProtected,
            editTextRes = editTextRes,
            deleteTextRes = deleteTextRes,
            onRename = onRename,
            onDelete = onDelete,
            modifier = Modifier.size(24.dp)
        )
    }
}

