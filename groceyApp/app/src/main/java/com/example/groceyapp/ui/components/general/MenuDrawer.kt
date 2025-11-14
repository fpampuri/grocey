package com.example.groceyapp.ui.components.general

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R
import com.example.groceyapp.ui.theme.BrandGreen
import com.example.groceyapp.ui.theme.BrandGreenDarkTheme
import java.util.Locale

/**
 * Sliding drawer menu that appears from the left side of the screen
 * Contains Profile and Settings sections
 */
@Composable
fun MenuDrawer(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit,
    userEmail: String = "user@gmail.com",
    userName: String = "John Doe",
    onNameChange: (String) -> Unit = {},
    isDarkMode: Boolean = false,
    onDarkModeToggle: (Boolean) -> Unit = {},
    currentLanguage: String = "en",
    onLanguageChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf(userName) }
    
    // Password change dialog
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword ->
                // TODO: Implement password change logic
                showPasswordDialog = false
            }
        )
    }
    // Scrim (dark overlay)
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Dark overlay that dismisses the drawer when clicked
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
            
            // Drawer content
            Column(
                modifier = modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Prevent clicks from dismissing */ }
                    )
                    .padding(vertical = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Section
                ProfileSection(
                    email = userEmail,
                    name = editableName,
                    onNameChange = { newName ->
                        editableName = newName
                        onNameChange(newName)
                    },
                    onPasswordChangeClick = { showPasswordDialog = true }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Settings Section
                SettingsSection(
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = onDarkModeToggle,
                    currentLanguage = currentLanguage,
                    onLanguageChange = onLanguageChange
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Logout Button
                Button(
                    onClick = {
                        onDismiss()
                        onLogoutClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.menu_logout))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Profile section with user information
 */
@Composable
private fun ProfileSection(
    email: String,
    name: String,
    onNameChange: (String) -> Unit,
    onPasswordChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (androidx.compose.foundation.isSystemInDarkTheme()) {
        BrandGreenDarkTheme
    } else {
        BrandGreen
    }
    
    var isEditingName by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_profile),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email field (non-editable) with green border and lock icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.profile_email_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = borderColor
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Name field (editable) with green border and edit icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditingName) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor,
                        cursorColor = borderColor
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.profile_name_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            IconButton(onClick = { isEditingName = !isEditingName }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.profile_change_password),
                    tint = borderColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Password field (non-editable with change button) with green border
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.profile_password_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "••••••••",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onPasswordChangeClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.profile_change_password),
                    tint = borderColor
                )
            }
        }
    }
}

/**
 * Settings section with app configuration
 */
@Composable
private fun SettingsSection(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLanguageMenu by remember { mutableStateOf(false) }
    
    val borderColor = if (androidx.compose.foundation.isSystemInDarkTheme()) {
        BrandGreenDarkTheme
    } else {
        BrandGreen
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_settings),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dark mode toggle with green border
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = borderColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.settings_dark_mode),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeToggle
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Language selector with green border
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { showLanguageMenu = true }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = borderColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_language),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = getLanguageDisplayName(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            DropdownMenu(
                expanded = showLanguageMenu,
                onDismissRequest = { showLanguageMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("English") },
                    onClick = {
                        onLanguageChange("en")
                        showLanguageMenu = false
                    },
                    leadingIcon = {
                        if (currentLanguage == "en") {
                            RadioButton(
                                selected = true,
                                onClick = null
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Español") },
                    onClick = {
                        onLanguageChange("es")
                        showLanguageMenu = false
                    },
                    leadingIcon = {
                        if (currentLanguage == "es") {
                            RadioButton(
                                selected = true,
                                onClick = null
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Dialog for changing password
 */
@Composable
private fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (oldPassword: String, newPassword: String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.dialog_change_password_title))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text(stringResource(R.string.dialog_old_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.dialog_new_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.dialog_confirm_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(oldPassword, newPassword) },
                enabled = oldPassword.isNotBlank() && 
                         newPassword.isNotBlank() && 
                         newPassword == confirmPassword
            ) {
                Text(stringResource(R.string.dialog_change))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

/**
 * Get display name for language code
 */
private fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
        "en" -> "English"
        "es" -> "Español"
        else -> languageCode
    }
}

/**
 * A section within the menu drawer (deprecated - kept for compatibility)
 */
@Composable
private fun DrawerSection(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        // Content for each section will be added here in the future
    }
}
