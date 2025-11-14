package com.example.groceyapp.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.GenericInputDialog

/**
 * Dialog for creating a new shopping list
 * Uses the generic GenericInputDialog component
 */
@Composable
fun CreateListDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, leadingIcon: ImageVector) -> Unit
) {
    GenericInputDialog(
        title = stringResource(id = R.string.add_list),
        inputLabel = stringResource(id = R.string.list_name_hint),
        confirmButtonText = stringResource(id = R.string.create),
        showIconPicker = true,
        onDismiss = onDismiss,
        onConfirm = { title, icon ->
            onCreate(title, icon ?: Icons.Rounded.ShoppingCart)
        }
    )
}
