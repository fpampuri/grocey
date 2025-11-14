package com.example.groceyapp.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groceyapp.R
import com.example.groceyapp.ui.components.general.ButtonVariant
import com.example.groceyapp.ui.components.general.StandardButton

/**
 * Confirmation dialog for deleting items (categories, lists, products, etc.)
 */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
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
                    variant = ButtonVariant.PRIMARY,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StandardButton(
                    title = stringResource(id = R.string.delete),
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    icon = Icons.Filled.Delete,
                    variant = ButtonVariant.DANGER,
                    modifier = Modifier.width(120.dp)
                )
            }
        },
        dismissButton = null
    )
}
