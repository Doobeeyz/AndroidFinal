package com.example.androidfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userId: Long,
    userEmail: String,
    onLogout: () -> Unit
) {
    // Загружаем данные пользователя при первом отображении экрана
    LaunchedEffect(Unit) {
        if (userId > 0) {
            viewModel.loadUserById(userId)
        } else if (userEmail.isNotEmpty()) {
            viewModel.loadUserByEmail(userEmail)
        }
    }

    // Получаем текущее состояние
    val userState by viewModel.userState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = userState) {
            is HomeViewModel.UserState.Loading -> {
                CircularProgressIndicator()
            }
            is HomeViewModel.UserState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ошибка: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onLogout) {
                        Text("Вернуться на экран регистрации")
                    }
                }
            }
            is HomeViewModel.UserState.Success -> {
                val user = state.user

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Добро пожаловать, ${user.username}!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Информация о пользователе",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Divider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Email:")
                                Text(user.email)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Дата рождения:")
                                Text(user.birthDate)
                            }

                            user.interests?.let {
                                if (it.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Интересы:")
                                        Text(it)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Вы успешно зарегистрировались и вошли в систему",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text("Выйти")
                    }
                }
            }
        }
    }
}