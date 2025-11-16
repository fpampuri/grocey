package com.example.groceyapp.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.GenericInputDialog

/**
 * Dialog for editing an existing pantry
 */
@Composable
fun EditPantryDialog(
    currentName: String,
    currentIcon: ImageVector?,
    onDismiss: () -> Unit,
    onUpdate: (name: String, icon: ImageVector) -> Unit
) {
    GenericInputDialog(
        title = stringResource(id = R.string.edit_pantry),
        inputLabel = stringResource(id = R.string.pantry_name_hint),
        confirmButtonText = stringResource(id = R.string.save),
        showIconPicker = true,
        onDismiss = onDismiss,
        onConfirm = { name, icon ->
            onUpdate(name, icon ?: currentIcon ?: Icons.Filled.Inventory2)
        }
    )
}
