package com.example.groceyapp.ui.components.general

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
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

@Composable
fun ListOptionsMenu(
    itemId: String,
    modifier: Modifier = Modifier,
    onRename: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

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
            text = { Text(stringResource(R.string.edit_list)) },
            onClick = {
                showMenu = false
                onRename(itemId)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.share_list)) },
            onClick = {
                showMenu = false
                onShare(itemId)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.delete_list)) },
            onClick = {
                showMenu = false
                onDelete(itemId)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            }
        )
    }
    }
}
