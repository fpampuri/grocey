package com.example.groceyapp.ui.components.general

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonVariant {
    PRIMARY,
    DANGER,
    OUTLINED
}

@Composable
fun StandardButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    backgroundColor: Color? = null
) {
    val defaultBackgroundColor = when (variant) {
        ButtonVariant.PRIMARY -> MaterialTheme.colorScheme.primary
        ButtonVariant.DANGER -> MaterialTheme.colorScheme.error
        ButtonVariant.OUTLINED -> MaterialTheme.colorScheme.surface
    }

    val resolvedContainerColor = backgroundColor ?: defaultBackgroundColor
    val resolvedContentColor = when (variant) {
        ButtonVariant.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        ButtonVariant.DANGER -> MaterialTheme.colorScheme.onError
        ButtonVariant.OUTLINED -> MaterialTheme.colorScheme.outline
    }
    
    val borderStroke = if (variant == ButtonVariant.OUTLINED) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    } else null
    
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = resolvedContainerColor,
            contentColor = resolvedContentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = borderStroke,
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 1.dp,
            disabledElevation = 1.dp
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
