package com.example.groceyapp.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.ButtonVariant
import com.example.groceyapp.ui.components.general.StandardButton

/**
 * Dialog for editing a product's name
 */
@Composable
fun EditProductDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.edit_product))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text(stringResource(id = R.string.product_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                StandardButton(
                    title = stringResource(id = R.string.cancel),
                    onClick = onDismiss,
                    icon = Icons.Filled.Close,
                    variant = ButtonVariant.DANGER,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StandardButton(
                    title = stringResource(id = R.string.save),
                    onClick = {
                        if (newName.trim().isNotBlank()) {
                            onUpdate(newName.trim())
                        }
                    },
                    icon = Icons.Filled.Check,
                    enabled = newName.trim().isNotBlank() && newName.trim() != currentName,
                    variant = ButtonVariant.PRIMARY,
                    modifier = Modifier.width(120.dp)
                )
            }
        },
        dismissButton = null
    )
}
