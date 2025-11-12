package com.example.groceyapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun PrimaryFab(
    icon: ImageVector = Icons.Default.Add,
    contentDescriptionRes: Int,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = contentDescriptionRes)
        )
    }
}
