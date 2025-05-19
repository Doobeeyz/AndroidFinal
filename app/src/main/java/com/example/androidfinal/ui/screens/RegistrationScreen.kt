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
import com.example.androidfinal.ui.viewmodels.RegistrationViewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onRegistrationSuccess: (userId: Long, email: String, username: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    // Наблюдаем состояние регистрации
    val registrationState by viewModel.registrationState.collectAsState()

    // Эффект для обработки успешной регистрации
    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationViewModel.RegistrationState.Success -> {
                val state = registrationState as RegistrationViewModel.RegistrationState.Success
                onRegistrationSuccess(state.userId, state.email, state.username)
                viewModel.resetState()
            }
            is RegistrationViewModel.RegistrationState.Error -> {
                formError = (registrationState as RegistrationViewModel.RegistrationState.Error).message
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
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                formError = null
            },
            label = { Text("Имя пользователя") },
            modifier = Modifier.fillMaxWidth(),
            isError = username.isBlank() && formError != null
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
            value = birthdate,
            onValueChange = {
                birthdate = it
                formError = null
            },
            label = { Text("Дата рождения (напр. 2000-01-01)") },
            modifier = Modifier.fillMaxWidth(),
            isError = birthdate.isBlank() && formError != null
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                formError = null
            },
            label = { Text("Подтвердите пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password != confirmPassword && formError != null
        )

        // Отображение ошибок
        formError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Отображение ошибок из ViewModel
        when (registrationState) {
            is RegistrationViewModel.RegistrationState.Error -> {
                Text(
                    text = (registrationState as RegistrationViewModel.RegistrationState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || email.isBlank() || birthdate.isBlank() || password.isBlank()) {
                    formError = "Пожалуйста, заполните все поля"
                } else if (!isValidEmail(email)) {
                    formError = "Введите корректный email"
                } else if (!isValidBirthdate(birthdate)) {
                    formError = "Введите корректную дату рождения (формат: ГГГГ-ММ-ДД)"
                } else if (password != confirmPassword) {
                    formError = "Пароли не совпадают"
                } else if (password.length < 6) {
                    formError = "Пароль должен содержать минимум 6 символов"
                } else {
                    formError = null
                    viewModel.registerUser(username, email, birthdate, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = registrationState !is RegistrationViewModel.RegistrationState.Loading
        ) {
            if (registrationState is RegistrationViewModel.RegistrationState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Зарегистрироваться")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onNavigateToLogin
        ) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}

// Функция для валидации email
private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}

// Функция для валидации даты рождения
private fun isValidBirthdate(birthdate: String): Boolean {
    val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    return dateRegex.matches(birthdate)
}