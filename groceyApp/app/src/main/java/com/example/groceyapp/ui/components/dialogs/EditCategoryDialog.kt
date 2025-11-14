package com.example.groceyapp.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.ButtonVariant
import com.example.groceyapp.ui.components.general.GenericInputDialog

@Composable
fun EditCategoryDialog(
    currentName: String = "",
    currentIcon: ImageVector? = null,
    onDismiss: () -> Unit,
    onUpdate: (String, ImageVector) -> Unit
) {
    GenericInputDialog(
        title = stringResource(id = R.string.edit_category),
        inputLabel = stringResource(id = R.string.category_name_hint),
        confirmButtonText = stringResource(id = R.string.save),
        showIconPicker = true,
        initialInputText = currentName,
        initialSelectedIcon = currentIcon ?: Icons.Rounded.Category,
        confirmIcon = Icons.Filled.Check,
        confirmButtonVariant = ButtonVariant.PRIMARY,
        onDismiss = onDismiss,
        onConfirm = { name, icon ->
            onUpdate(name, icon ?: Icons.Rounded.Category)
        }
    )
}
