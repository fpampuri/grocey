package com.example.groceyapp.ui.products

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.GenericInputDialog

/**
 * Dialog for creating a new category
 */
@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, icon: ImageVector?) -> Unit
) {
    GenericInputDialog(
        title = stringResource(id = R.string.add_category),
        inputLabel = stringResource(id = R.string.category_name_hint),
        confirmButtonText = stringResource(id = R.string.create),
        showIconPicker = true,
        onDismiss = onDismiss,
        onConfirm = { name, icon ->
            onCreate(name, icon ?: Icons.Filled.Store)
        }
    )
}
