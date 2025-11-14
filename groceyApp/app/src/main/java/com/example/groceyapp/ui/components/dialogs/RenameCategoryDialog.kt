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
 * Dialog for renaming a category
 */
@Composable
fun RenameCategoryDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.rename_category))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text(stringResource(id = R.string.category_name_hint)) },
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
                    title = stringResource(id = android.R.string.cancel),
                    onClick = onDismiss,
                    icon = Icons.Filled.Close,
                    variant = ButtonVariant.PRIMARY
                )
                Spacer(modifier = Modifier.width(8.dp))
                StandardButton(
                    title = stringResource(id = R.string.save),
                    onClick = {
                        onRename(newName.trim())
                    },
                    icon = Icons.Filled.Check,
                    enabled = newName.trim().isNotBlank() && newName.trim() != currentName
                )
            }
        },
        dismissButton = null
    )
}
