package com.example.groceyapp.ui.components.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun AdaptiveNavigationContainer(
    isLandscape: Boolean,
    paddingValues: PaddingValues,
    navRailWidth: Dp = HomeNavigationRailWidth,
    navigationRail: @Composable (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    if (isLandscape && navigationRail != null) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                content(Modifier.fillMaxSize())
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(navRailWidth)
            ) {
                navigationRail()
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}
