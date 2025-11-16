package com.example.groceyapp.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groceyapp.R

/**
 * Dialog for sharing a shopping list with other users
 */
@Composable
fun ShareListDialog(
    listName: String,
    onDismiss: () -> Unit,
    onShare: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf("") }
    var emailList by remember { mutableStateOf<List<String>>(emptyList()) }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.share_list_title, listName),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.share_list_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Email input section
                Text(
                    text = stringResource(R.string.add_email_address),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { 
                            emailInput = it
                            emailError = null
                        },
                        placeholder = { 
                            Text(stringResource(R.string.enter_email_address)) 
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = emailError?.let { 
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    
                    IconButton(
                        onClick = {
                            val trimmedEmail = emailInput.trim()
                            when {
                                trimmedEmail.isEmpty() -> {
                                    emailError = "Email cannot be empty"
                                }
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                                    emailError = "Invalid email format"
                                }
                                emailList.contains(trimmedEmail) -> {
                                    emailError = "Email already added"
                                }
                                else -> {
                                    emailList = emailList + trimmedEmail
                                    emailInput = ""
                                    emailError = null
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_email),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // List of added emails
                if (emailList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn(
                        modifier = Modifier.height(150.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(emailList) { email ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                IconButton(
                                    onClick = {
                                        emailList = emailList - email
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.remove_email),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (emailList.isNotEmpty()) {
                        onShare(emailList)
                    }
                },
                enabled = emailList.isNotEmpty(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(
                        R.string.share_with_count,
                        emailList.size
                    )
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        modifier = modifier
    )
}
