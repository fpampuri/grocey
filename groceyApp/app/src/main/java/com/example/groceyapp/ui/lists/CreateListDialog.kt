package com.example.groceyapp.ui.lists

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R

@Composable
fun CreateListDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, leadingIcon: ImageVector) -> Unit
) {
    val titleState = remember { mutableStateOf("") }

    // Mapped icon list (order follows the mdi list provided). Each mdi -> closest
    // available Material icon. The list is deduplicated and stable.
    // Fallback mapping using icons we know are available in the project.
    val icons = listOf(
        Icons.Rounded.ShoppingCart,
        Icons.Filled.Store,
        Icons.Filled.Inventory2,
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Star,
        Icons.Outlined.StarBorder,
        Icons.Filled.Add,
        Icons.Filled.Remove,
        Icons.Filled.MoreVert,
        Icons.Filled.Delete,
        Icons.Filled.Edit,
        Icons.Filled.Share,
        Icons.Filled.FilterList,
        Icons.Filled.Search,
        Icons.Filled.ChevronRight,
        Icons.Filled.Circle,
        Icons.Filled.Person,
        Icons.Filled.Email,
        Icons.Filled.Lock,
        Icons.Filled.DarkMode,
        Icons.Filled.Language,
        Icons.Filled.Visibility,
        Icons.Filled.VisibilityOff,
        Icons.AutoMirrored.Filled.ArrowBack,
        Icons.Filled.Home,
        Icons.Filled.Bookmark,
        Icons.Filled.LocalOffer,
        Icons.Filled.Label,
        Icons.Filled.Bookmark,
        Icons.Filled.Store,
        Icons.Filled.ShoppingCart,
        Icons.Filled.Inventory2,
        Icons.Filled.Inventory2,
        Icons.Filled.ShoppingCart,
        Icons.Filled.Star
    ).distinct()

    val selectedIcon = remember { mutableStateOf(icons.first()) }
    var showIconPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.add_list))
        },
        text = {
            Column {
                TextField(
                    value = titleState.value,
                    onValueChange = { titleState.value = it },
                    placeholder = { Text(text = stringResource(id = R.string.add_list)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Icon picker trigger (behaves like a dropdown). Opens a full dialog
                // showing a grid of available icons for better discoverability.
                OutlinedButton(onClick = { showIconPicker = true }, modifier = Modifier) {
                    Icon(imageVector = selectedIcon.value, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.select_icon))
                }

                if (showIconPicker) {
                    AlertDialog(
                        onDismissRequest = { showIconPicker = false },
                        title = { Text(text = stringResource(id = R.string.select_icon)) },
                        text = {
                            // Simple grid: chunk the icons into rows of 5
                            Column {
                                icons.chunked(5).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        row.forEach { icon ->
                                            val isSelected = icon == selectedIcon.value
                                            Surface(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .then(
                                                        if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium) else Modifier
                                                    ),
                                                color = MaterialTheme.colorScheme.surface
                                            ) {
                                                IconButton(onClick = {
                                                    selectedIcon.value = icon
                                                    showIconPicker = false
                                                }) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showIconPicker = false }) { Text(text = stringResource(id = android.R.string.ok)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showIconPicker = false }) { Text(text = stringResource(id = android.R.string.cancel)) }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(titleState.value.trim(), selectedIcon.value) },
                enabled = titleState.value.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.add_list))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}
