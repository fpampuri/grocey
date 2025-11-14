package com.example.groceyapp.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.ButtonVariant
import com.example.groceyapp.ui.components.general.GenericInputDialog

@Composable
fun EditListDialog(
    currentName: String = "",
    currentIcon: ImageVector? = null,
    onDismiss: () -> Unit,
    onUpdate: (String, ImageVector) -> Unit
) {
    GenericInputDialog(
        title = stringResource(id = R.string.edit_list),
        inputLabel = stringResource(id = R.string.list_name_hint),
        confirmButtonText = stringResource(id = R.string.save),
        showIconPicker = true,
        initialInputText = currentName,
        initialSelectedIcon = currentIcon ?: Icons.Rounded.ShoppingCart,
        confirmIcon = Icons.Filled.Check,
        confirmButtonVariant = ButtonVariant.PRIMARY,
        onDismiss = onDismiss,
        onConfirm = { name, icon ->
            onUpdate(name, icon ?: Icons.Rounded.ShoppingCart)
        }
    )
}
