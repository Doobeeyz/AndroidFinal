package com.example.androidfinal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidfinal.ui.viewmodels.CommentViewModel
import com.example.androidfinal.ui.viewmodels.HomeViewModel
import com.example.androidfinal.ui.viewmodels.PostViewModel


@Composable
fun UserProfileScreen(
    userId: Long,
    postViewModel: PostViewModel,
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    commentViewModel: CommentViewModel
) {
    val postState by postViewModel.postState.collectAsState()
    var username by remember { mutableStateOf("Загрузка...") }



    LaunchedEffect(userId) {
        postViewModel.loadPosts(userId)
        username = homeViewModel.getUserName(userId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Профиль пользователя: $username", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (val state = postState) {
            is PostViewModel.PostState.Loading -> CircularProgressIndicator()
            is PostViewModel.PostState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.posts.size) { index ->
                        val post = state.posts[index]
                        PostCard(
                            post = post,
                            commentViewModel = commentViewModel,
                            homeViewModel = homeViewModel,
                            currentUserId = userId
                        )
                    }
                }
            }
            is PostViewModel.PostState.Error -> Text("Ошибка: ${state.message}")
            else -> Text("Нет постов")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}
