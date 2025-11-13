package com.example.groceyapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryFab(
    icon: ImageVector = Icons.Default.Add,
    contentDescriptionRes: Int,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF66BB6A), // Lighter green color
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = contentDescriptionRes)
        )
    }
}
