package com.example.androidfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.HomeViewModel
import androidx.compose.foundation.lazy.items

@Composable
fun ProfileScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {





    val userState by viewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        if (userState is HomeViewModel.UserState.Success) {
            viewModel.loadAllPosts()
        }
    }


    val posts = remember(userState) {
        if (userState is HomeViewModel.UserState.Success) {
            viewModel.getPostsByUser((userState as HomeViewModel.UserState.Success).user.id)
        } else emptyList()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val state = userState) {

            is HomeViewModel.UserState.Loading -> {
                item {
                    CircularProgressIndicator(color = Color(0xFF2A4174))
                }
            }

            is HomeViewModel.UserState.Error -> {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Ошибка: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A4174)
                            )
                        ) {
                            Text("Вернуться к входу", color = Color.White)
                        }
                    }
                }
            }

            is HomeViewModel.UserState.Success -> {
                val user = state.user

                item {
                    Text(
                        text = "Добро пожаловать, ${user.username}!",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE1E2EC)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Информация о пользователе",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )

                            HorizontalDivider()

                            InfoRow("Имя пользователя:", user.username)
                            InfoRow("Email:", user.email)
                            InfoRow("Дата рождения:", user.birthDate)

                            user.interests?.takeIf { it.isNotEmpty() }?.let {
                                InfoRow("Интересы:", it)
                            }
                        }
                    }
                }

                if (posts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Ваши посты",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(posts) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF3F4F6)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = post.title, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = post.content, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("У вас пока нет постов", color = Color.Gray)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A4174)
                        )
                    ) {
                        Text("Выйти из аккаунта", color = Color.White)
                    }
                }
            }

            HomeViewModel.UserState.Idle -> {
                item {}
            }
        }
    }
}



@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

