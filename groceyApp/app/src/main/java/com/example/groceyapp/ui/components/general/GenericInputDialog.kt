package com.example.groceyapp.ui.components.general

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * Generic reusable dialog for creating items with a name/title and optional icon
 * 
 * @param title The dialog title (e.g., "Add List", "Add Product")
 * @param inputLabel The label/placeholder for the text field
 * @param confirmButtonText Text for the confirm button
 * @param showIconPicker Whether to show the icon picker option
 * @param defaultIcons Optional list of default icons to show in picker
 * @param onDismiss Callback when dialog is dismissed/cancelled
 * @param onConfirm Callback when confirmed with (inputText, selectedIcon?)
 */
@Composable
fun GenericInputDialog(
    title: String,
    inputLabel: String,
    confirmButtonText: String,
    showIconPicker: Boolean = false,
    defaultIcons: List<ImageVector>? = null,
    initialInputText: String = "",
    initialSelectedIcon: ImageVector? = null,
    confirmIcon: ImageVector? = null,
    confirmButtonVariant: ButtonVariant = ButtonVariant.PRIMARY,
    onDismiss: () -> Unit,
    onConfirm: (String, ImageVector?) -> Unit
) {
    var inputText by remember { mutableStateOf(initialInputText) }
    var isIconPickerOpen by remember { mutableStateOf(false) }

    val icons = defaultIcons ?: getDefaultIcons()
    var selectedIcon by remember { mutableStateOf(initialSelectedIcon ?: icons.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(text = inputLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (showIconPicker) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { isIconPickerOpen = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = selectedIcon, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = com.example.groceyapp.R.string.select_icon))
                    }
                }

                if (isIconPickerOpen) {
                    IconPickerDialog(
                        icons = icons,
                        selectedIcon = selectedIcon,
                        onIconSelected = { icon ->
                            selectedIcon = icon
                            isIconPickerOpen = false
                        },
                        onDismiss = { isIconPickerOpen = false }
                    )
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
                    title = confirmButtonText,
                    onClick = {
                        val icon = if (showIconPicker) selectedIcon else null
                        onConfirm(inputText.trim(), icon)
                    },
                    icon = confirmIcon ?: Icons.Filled.Add,
                    variant = confirmButtonVariant,
                    backgroundColor = if (confirmButtonVariant == ButtonVariant.PRIMARY) MaterialTheme.colorScheme.primary else null,
                    enabled = inputText.isNotBlank()
                )
            }
        },
        dismissButton = null
    )
}

/**
 * Icon picker dialog showing a grid of available icons
 */
@Composable
private fun IconPickerDialog(
    icons: List<ImageVector>,
    selectedIcon: ImageVector,
    onIconSelected: (ImageVector) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = com.example.groceyapp.R.string.select_icon))
        },
        text = {
            Column {
                icons.chunked(5).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { icon ->
                            val isSelected = icon == selectedIcon
                            Surface(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(
                                                2.dp,
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.shapes.medium
                                            )
                                        } else {
                                            Modifier
                                        }
                                    ),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                IconButton(onClick = { onIconSelected(icon) }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            StandardButton(
                title = stringResource(id = android.R.string.ok),
                onClick = onDismiss,
                icon = Icons.Filled.Check
            )
        },
        dismissButton = null
    )
}

/**
 * Default icon set for the icon picker
 */
private fun getDefaultIcons(): List<ImageVector> {
    return listOf(
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
        Icons.Filled.Tag,
        Icons.Rounded.LocalGroceryStore,
        Icons.Filled.Favorite,
        Icons.Filled.FavoriteBorder,
        Icons.Filled.ShoppingBasket,
        Icons.Filled.Restaurant,
        Icons.Filled.Fastfood,
        Icons.Filled.LocalDining
    ).distinct()
}
