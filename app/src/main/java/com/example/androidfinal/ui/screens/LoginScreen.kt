package com.example.androidfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: com.example.androidfinal.ui.viewmodels.LoginViewModel,
    onLoginSuccess: (userId: Long, email: String, username: String) -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }


    val loginState by viewModel.loginState.collectAsState()


    LaunchedEffect(loginState) {
        when (loginState) {
            is com.example.androidfinal.ui.viewmodels.LoginViewModel.LoginState.Success -> {
                val state = loginState as com.example.androidfinal.ui.viewmodels.LoginViewModel.LoginState.Success
                onLoginSuccess(state.userId, state.email, state.username)
                viewModel.resetState()
            }
            is com.example.androidfinal.ui.viewmodels.LoginViewModel.LoginState.Error -> {
                formError = (loginState as com.example.androidfinal.ui.viewmodels.LoginViewModel.LoginState.Error).message
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вход в аккаунт",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
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
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
            isError = email.isBlank() && formError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                focusedBorderColor = Color(0xFF2A4174),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                formError = null
            },
            label = { Text("Пароль") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isBlank() && formError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                focusedBorderColor = Color(0xFF2A4174),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black
            )
        )


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
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A4174)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = loginState !is com.example.androidfinal.ui.viewmodels.LoginViewModel.LoginState.Loading
        ) {
            if (loginState is com.example.androidfinal.ui.viewmodels.LoginViewModel.LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Войти", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToRegistration,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Нет аккаунта? Зарегистрироваться", color = Color(0xFF2A4174))
        }
    }
}


private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}