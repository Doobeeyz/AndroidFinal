package com.example.androidfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.HomeViewModel
import com.example.androidfinal.ui.viewmodels.PostViewModel

@Composable
fun CreatePostScreen(
    viewModel: HomeViewModel,
    postViewModel: PostViewModel,
    userId: Long,
    modifier: Modifier = Modifier
) {
    val userState by viewModel.userState.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Создать новый пост",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        when (val state = userState) {
            is HomeViewModel.UserState.Success -> {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок поста") },
                    modifier = Modifier.fillMaxWidth(),
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

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Содержимое поста") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10,
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

                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            postViewModel.createPost(userId, title, content)
                            title = ""
                            content = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A4174)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && content.isNotBlank()
                ) {
                    Text("Опубликовать пост", color = Color.White)
                }

                if (title.isBlank() || content.isBlank()) {
                    Text(
                        text = "Заполните все поля для публикации поста",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            is HomeViewModel.UserState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2A4174))
                }
            }

            is HomeViewModel.UserState.Error -> {
                Text(
                    text = "Ошибка загрузки данных пользователя: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            HomeViewModel.UserState.Idle -> TODO()
        }
    }
}