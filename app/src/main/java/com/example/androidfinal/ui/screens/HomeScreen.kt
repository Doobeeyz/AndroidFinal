package com.example.androidfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.androidfinal.ui.viewmodels.CommentViewModel
import com.example.androidfinal.ui.viewmodels.HomeViewModel
import com.example.androidfinal.ui.viewmodels.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel,
    userId: Long,
    userEmail: String,
    onLogout: () -> Unit,
    onUserClick: (Long) -> Unit
) {

    var selectedTab by remember { mutableStateOf(0) }

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

    Scaffold(
        modifier = Modifier.background(Color.White),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFE1E2EC)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Лента") },
                    label = { Text("Лента") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2A4174),
                        selectedTextColor = Color(0xFF2A4174),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF2A4174).copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Создать") },
                    label = { Text("Создать") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2A4174),
                        selectedTextColor = Color(0xFF2A4174),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF2A4174).copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                    label = { Text("Профиль") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2A4174),
                        selectedTextColor = Color(0xFF2A4174),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF2A4174).copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { innerPadding ->

        when (selectedTab) {
            0 -> PostFeedScreen(
                postViewModel = postViewModel,
                commentViewModel = commentViewModel,
                homeViewModel = viewModel,
                currentUserId = userId,
                onUserClick = onUserClick,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> CreatePostScreen(
                viewModel = viewModel,
                postViewModel = postViewModel,
                userId = userId,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> ProfileScreen(
                viewModel = viewModel,
                onLogout = onLogout,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}