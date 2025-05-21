package com.example.androidfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.CommentViewModel
import com.example.androidfinal.ui.viewmodels.HomeViewModel
import com.example.androidfinal.ui.viewmodels.PostViewModel

@Composable

fun HomeScreen(
    viewModel: HomeViewModel,
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel,
    userId: Long,
    userEmail: String,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (userId > 0) {
            viewModel.loadUserById(userId)
        } else if (userEmail.isNotEmpty()) {
            viewModel.loadUserByEmail(userEmail)
        }
    }

    LaunchedEffect(userId) {
        postViewModel.loadAllPosts()
    }

    val userState by viewModel.userState.collectAsState()
    val postState by postViewModel.postState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when (val state = userState) {
            is HomeViewModel.UserState.Loading -> {
                CircularProgressIndicator()
            }

            is HomeViewModel.UserState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
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
                var title by remember { mutableStateOf("") }
                var content by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Добро пожаловать, ${user.username}!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Информация о пользователе", style = MaterialTheme.typography.titleMedium)
                            Divider()
                            InfoRow("Email:", user.email)
                            InfoRow("Дата рождения:", user.birthDate)
                            user.interests?.takeIf { it.isNotEmpty() }?.let {
                                InfoRow("Интересы:", it)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Новая форма создания поста
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Создать новый пост", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Заголовок") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Содержимое") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )

                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    postViewModel.createPost(user.id, title, content)
                                    title = ""
                                    content = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Опубликовать")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Список постов
                    Text("Все посты:", style = MaterialTheme.typography.titleMedium)

                    when (val post = postState) {
                        is PostViewModel.PostState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is PostViewModel.PostState.Success -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(post.posts.size) { index ->
                                    val postItem = post.posts[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(postItem.title, style = MaterialTheme.typography.titleMedium)
                                            Text(postItem.content)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            PostCommentsSection(postId = postItem.id, commentViewModel = commentViewModel)
                                        }
                                    }
                                }
                            }
                        }

                        is PostViewModel.PostState.Error -> {
                            Text("Ошибка загрузки постов: ${post.message}", color = MaterialTheme.colorScheme.error)
                        }

                        else -> {}
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Вы успешно зарегистрировались и вошли в систему",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(onClick = onLogout) {
                        Text("Выйти")
                    }
                }
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
        Text(label)
        Text(value)
    }
}


@Composable
fun PostCommentsSection(
    postId: Long,
    commentViewModel: CommentViewModel
) {
    LaunchedEffect(postId) {
        commentViewModel.loadComments(postId)
    }

    val commentState by commentViewModel.commentState.collectAsState()

    var author by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        when (val state = commentState) {
            is CommentViewModel.CommentState.Loading -> {
                Text("Загрузка комментариев...")
            }

            is CommentViewModel.CommentState.Success -> {
                Text("Комментарии:", style = MaterialTheme.typography.bodyMedium)
                state.comments.forEach { comment ->
                    Text("• ${comment.author}: ${comment.text}")
                }
            }

            is CommentViewModel.CommentState.Error -> {
                Text("Ошибка: ${state.message}", color = MaterialTheme.colorScheme.error)
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Добавить комментарий:", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Ваше имя") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Комментарий") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Button(
            onClick = {
                if (author.isNotBlank() && commentText.isNotBlank()) {
                    commentViewModel.addComment(postId, author, commentText)
                    author = ""
                    commentText = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Отправить")
        }
    }
}



