package com.example.groceyapp.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarDuration
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groceyapp.R
import com.example.groceyapp.ui.theme.GroceyAppTheme
import com.example.groceyapp.ui.viewmodel.AuthViewModel

enum class AuthMode {
    LOGIN, REGISTER, VERIFY
}

/**
 * Authentication screen with login, registration, and verification modes
 * Integrated with AuthViewModel for real API calls
 */
@Composable
fun AuthenticationScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var verificationToken by remember { mutableStateOf("") }
    
    val focusManager = LocalFocusManager.current
    
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    val greenColor = MaterialTheme.colorScheme.primary
    
    // Handle authentication success
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onLoginSuccess()
        }
    }
    
    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Title
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (authMode) {
                    AuthMode.LOGIN -> stringResource(R.string.auth_welcome_message)
                    AuthMode.REGISTER -> stringResource(R.string.register_welcome_message)
                    AuthMode.VERIFY -> stringResource(R.string.verify_welcome_message)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            when (authMode) {
                AuthMode.LOGIN -> {
                    LoginContent(
                        email = email,
                        password = password,
                        passwordVisible = passwordVisible,
                        isLoading = isLoading,
                        greenColor = greenColor,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                        onLogin = { viewModel.login(email, password) },
                        onSwitchToRegister = {
                            authMode = AuthMode.REGISTER
                            password = ""
                        },
                        focusManager = focusManager
                    )
                }
                AuthMode.REGISTER -> {
                    RegisterContent(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        passwordVisible = passwordVisible,
                        isLoading = isLoading,
                        greenColor = greenColor,
                        onFirstNameChange = { firstName = it },
                        onLastNameChange = { lastName = it },
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                        onRegister = {
                            viewModel.register(email, firstName, lastName, password) {
                                authMode = AuthMode.VERIFY
                            }
                        },
                        onSwitchToLogin = {
                            authMode = AuthMode.LOGIN
                            password = ""
                        },
                        focusManager = focusManager
                    )
                }
                AuthMode.VERIFY -> {
                    VerifyContent(
                        verificationCode = verificationCode,
                        isLoading = isLoading,
                        greenColor = greenColor,
                        onCodeChange = { verificationCode = it },
                        onVerify = {
                            viewModel.verifyAccount(verificationCode)
                        },
                        onResendCode = {
                            viewModel.register(email, firstName, lastName, password) {
                                // Verification code resent to email
                            }
                        },
                        focusManager = focusManager
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    greenColor: androidx.compose.ui.graphics.Color,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLogin: () -> Unit,
    onSwitchToRegister: () -> Unit,
    focusManager: FocusManager
) {
    // Email Field
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.auth_email_label)) },
        placeholder = { Text(stringResource(R.string.auth_email_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = greenColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Password Field
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.auth_password_label)) },
        placeholder = { Text(stringResource(R.string.auth_password_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = greenColor
            )
        },
        trailingIcon = {
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(
                    imageVector = if (passwordVisible) 
                        Icons.Default.Visibility 
                    else 
                        Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible)
                        stringResource(R.string.auth_hide_password)
                    else
                        stringResource(R.string.auth_show_password),
                    tint = greenColor
                )
            }
        },
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                if (email.isNotBlank() && password.isNotBlank() && !isLoading) {
                    onLogin()
                }
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Login Button
    Button(
        onClick = onLogin,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = greenColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.auth_login_button),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Switch to Register
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.auth_no_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.auth_create_account),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = greenColor,
            modifier = Modifier
                .clickable(enabled = !isLoading) { onSwitchToRegister() }
                .padding(start = 4.dp)
        )
    }
}

@Composable
private fun RegisterContent(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    greenColor: androidx.compose.ui.graphics.Color,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onRegister: () -> Unit,
    onSwitchToLogin: () -> Unit,
    focusManager: FocusManager
) {
    // First Name Field
    OutlinedTextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        label = { Text(stringResource(R.string.register_first_name_label)) },
        placeholder = { Text(stringResource(R.string.register_first_name_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = greenColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Last Name Field
    OutlinedTextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = { Text(stringResource(R.string.register_last_name_label)) },
        placeholder = { Text(stringResource(R.string.register_last_name_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = greenColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Email Field
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.auth_email_label)) },
        placeholder = { Text(stringResource(R.string.auth_email_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = greenColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Password Field
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.auth_password_label)) },
        placeholder = { Text(stringResource(R.string.auth_password_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = greenColor
            )
        },
        trailingIcon = {
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(
                    imageVector = if (passwordVisible) 
                        Icons.Default.Visibility 
                    else 
                        Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible)
                        stringResource(R.string.auth_hide_password)
                    else
                        stringResource(R.string.auth_show_password),
                    tint = greenColor
                )
            }
        },
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                if (firstName.isNotBlank() && lastName.isNotBlank() && 
                    email.isNotBlank() && password.isNotBlank() && !isLoading) {
                    onRegister()
                }
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Register Button
    Button(
        onClick = onRegister,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = firstName.isNotBlank() && lastName.isNotBlank() && 
                  email.isNotBlank() && password.isNotBlank() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = greenColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.register_button),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Switch to Login
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.auth_have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.auth_sign_in),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = greenColor,
            modifier = Modifier
                .clickable(enabled = !isLoading) { onSwitchToLogin() }
                .padding(start = 4.dp)
        )
    }
}

@Composable
private fun VerifyContent(
    verificationCode: String,
    isLoading: Boolean,
    greenColor: androidx.compose.ui.graphics.Color,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResendCode: () -> Unit,
    focusManager: FocusManager
) {
    // Verification Code Field
    OutlinedTextField(
        value = verificationCode,
        onValueChange = onCodeChange,
        label = { Text(stringResource(R.string.verify_code_label)) },
        placeholder = { Text(stringResource(R.string.verify_code_placeholder)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                if (verificationCode.isNotBlank() && !isLoading) {
                    onVerify()
                }
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            unfocusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Verify Button
    Button(
        onClick = onVerify,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = verificationCode.isNotBlank() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = greenColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.verify_button),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Resend Code
    Text(
        text = stringResource(R.string.verify_resend),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = greenColor,
        modifier = Modifier
            .clickable(enabled = !isLoading) { onResendCode() }
            .padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AuthenticationScreenPreview() {
    GroceyAppTheme {
        AuthenticationScreen(
            onLoginSuccess = { }
        )
    }
}
