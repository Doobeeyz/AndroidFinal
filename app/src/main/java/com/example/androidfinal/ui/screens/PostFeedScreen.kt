package com.example.androidfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.CommentViewModel
import com.example.androidfinal.ui.viewmodels.PostViewModel
import com.example.androidfinal.ui.viewmodels.HomeViewModel

@Composable
fun PostFeedScreen(
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel,
    homeViewModel: HomeViewModel,
    currentUserId: Long,
    modifier: Modifier = Modifier,
    onUserClick: (Long) -> Unit,

) {
    val postState by postViewModel.postState.collectAsState()


    LaunchedEffect(Unit) {
        postViewModel.loadAllPosts()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Лента постов",
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = postState) {
            is PostViewModel.PostState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2A4174))
                }
            }

            is PostViewModel.PostState.Success -> {
                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.posts.size) { index ->
                        val post = state.posts[index]
                        PostCard(
                            post = post,
                            commentViewModel = commentViewModel,
                            homeViewModel = homeViewModel,
                            currentUserId = currentUserId,
                            onUsernameClick = onUserClick
                        )
                    }
                }
            }

            is PostViewModel.PostState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ошибка загрузки постов",
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Нет постов для отображения", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: com.example.androidfinal.data.models.Post,
    commentViewModel: CommentViewModel,
    homeViewModel: HomeViewModel,
    currentUserId: Long,
    onUsernameClick: ((Long) -> Unit)? = null
) {
    var authorName by remember { mutableStateOf("Загрузка...") }

    LaunchedEffect(post.userId) {
        authorName = homeViewModel.getUserName(post.userId)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE1E2EC)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Автор: $authorName",
                modifier = Modifier.clickable {
                    onUsernameClick?.invoke(post.userId)
                },
                color = Color(0xFF2A4174),
                style = MaterialTheme.typography.bodySmall
            )



            Text(
                text = post.title,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = post.content,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            PostCommentsSection(
                postId = post.id,
                commentViewModel = commentViewModel,
                homeViewModel = homeViewModel,
                currentUserId = currentUserId
            )
        }
    }
}

@Composable
fun PostCommentsSection(
    postId: Long,
    commentViewModel: CommentViewModel,
    homeViewModel: HomeViewModel,
    currentUserId: Long
) {
    LaunchedEffect(postId) {
        commentViewModel.loadComments(postId)
    }

    val commentStates by commentViewModel.commentStates.collectAsState()
    val commentState = commentStates[postId] ?: CommentViewModel.CommentState.Idle
    val userState by homeViewModel.userState.collectAsState()
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Комментарии",
            color = Color.Black,
            style = MaterialTheme.typography.titleSmall
        )

        when (val state = commentState) {
            is CommentViewModel.CommentState.Loading -> {
                Text(
                    text = "Загрузка комментариев...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }

            is CommentViewModel.CommentState.Success -> {
                if (state.comments.isNotEmpty()) {
                    state.comments.forEach { comment ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = comment.author,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2A4174),
                                )
                                Text(
                                    text = comment.text,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Комментариев пока нет",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                    )
                }
            }

            is CommentViewModel.CommentState.Error -> {
                Text(
                    text = "Ошибка загрузки: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            else -> {}
        }

        Text(
            text = "Добавить комментарий",
            color = Color.Black,
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Текст комментария") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
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
                when (val state = userState) {
                    is HomeViewModel.UserState.Success -> {
                        if (commentText.isNotBlank()) {
                            commentViewModel.addComment(postId, state.user.username, commentText)
                            commentText = ""
                        }
                    }
                    else -> {}
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A4174)
            ),
            modifier = Modifier.align(androidx.compose.ui.Alignment.End),
            enabled = commentText.isNotBlank() && userState is HomeViewModel.UserState.Success
        ) {
            Text(
                text = "Отправить",
                color = Color.White
            )
        }
    }
}