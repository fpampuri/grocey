package com.example.groceyapp.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.StandardButton
import com.example.groceyapp.ui.components.general.ButtonVariant

@Composable
fun AddProductToCategoryDialog(
    categoryName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var productName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.add_product_to_category, categoryName))
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.product_name_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    placeholder = {
                        Text(text = stringResource(id = R.string.product_name_hint))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            StandardButton(
                title = stringResource(id = R.string.add),
                onClick = {
                    if (productName.isNotBlank()) {
                        onConfirm(productName.trim())
                    }
                },
                enabled = productName.isNotBlank(),
                variant = ButtonVariant.PRIMARY
            )
        },
        dismissButton = {
            StandardButton(
                title = stringResource(id = R.string.cancel),
                onClick = onDismiss,
                variant = ButtonVariant.OUTLINED
            )
        }
    )
}
