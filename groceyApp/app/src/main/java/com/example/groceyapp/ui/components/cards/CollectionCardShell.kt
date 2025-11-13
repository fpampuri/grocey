package com.example.groceyapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment.Companion.CenterVertically

/**
 * Small wrapper that centralizes common OutlinedCard styling used by collection-type cards
 * (Lists, Categories). Use this to guarantee identical outer padding, icon sizes and
 * button sizes so card heights stay consistent across collection screens.
 */
@Composable
fun CollectionCardShell(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(contentPadding),
            verticalAlignment = CenterVertically,
            content = content
        )
    }
}
