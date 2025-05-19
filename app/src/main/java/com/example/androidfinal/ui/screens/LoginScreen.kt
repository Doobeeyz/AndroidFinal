package com.example.androidfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (userId: Long, email: String, username: String) -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    // Наблюдаем состояние входа
    val loginState by viewModel.loginState.collectAsState()

    // Эффект для обработки успешного входа
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginViewModel.LoginState.Success -> {
                val state = loginState as LoginViewModel.LoginState.Success
                onLoginSuccess(state.userId, state.email, state.username)
                viewModel.resetState()
            }
            is LoginViewModel.LoginState.Error -> {
                formError = (loginState as LoginViewModel.LoginState.Error).message
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вход в аккаунт",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                formError = null
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isBlank() && formError != null
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                formError = null
            },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isBlank() && formError != null
        )

        // Отображение ошибок
        formError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    formError = "Пожалуйста, заполните все поля"
                } else if (!isValidEmail(email)) {
                    formError = "Введите корректный email"
                } else {
                    formError = null
                    viewModel.loginUser(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = loginState !is LoginViewModel.LoginState.Loading
        ) {
            if (loginState is LoginViewModel.LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Войти")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToRegistration,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Нет аккаунта? Зарегистрироваться")
        }
    }
}

// Функция для валидации email
private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}