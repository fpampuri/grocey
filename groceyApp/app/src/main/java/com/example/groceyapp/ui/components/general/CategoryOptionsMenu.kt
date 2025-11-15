package com.example.groceyapp.ui.components.general

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.groceyapp.R
import com.example.groceyapp.Constants

@Composable
fun CategoryOptionsMenu(
    categoryId: Long?,
    isProtected: Boolean = false,
    modifier: Modifier = Modifier,
    onRename: (Long?) -> Unit = {},
    onDelete: (Long?) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val isMiscellaneous = isProtected || categoryId == Constants.MISCELLANEOUS_CATEGORY_ID

    // Don't show menu at all for Miscellaneous category
    if (isMiscellaneous) {
        return
    }

    // Anchor menu to the icon button so it opens below the button
    Box {
        IconButton(
            onClick = { showMenu = !showMenu },
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more_options),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = DpOffset(x = 0.dp, y = 4.dp)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit_category)) },
                onClick = {
                    showMenu = false
                    onRename(categoryId)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete_category)) },
                onClick = {
                    showMenu = false
                    onDelete(categoryId)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}
