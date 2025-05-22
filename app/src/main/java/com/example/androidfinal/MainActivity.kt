package com.example.androidfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidfinal.ui.screens.HomeScreen
import com.example.androidfinal.ui.screens.LoginScreen
import com.example.androidfinal.ui.screens.RegistrationScreen
import com.example.androidfinal.ui.theme.AndroidFinalTheme
import com.example.androidfinal.ui.viewmodels.CommentViewModel
import com.example.androidfinal.ui.viewmodels.HomeViewModel
import com.example.androidfinal.ui.viewmodels.LoginViewModel
import com.example.androidfinal.ui.viewmodels.PostViewModel
import com.example.androidfinal.ui.viewmodels.RegistrationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Получение экземпляра репозитория из Application класса
        val userRepository = (application as MyApplication).userRepository
        val postRepository = (application as MyApplication).postRepository
        val commentRepository = (application as MyApplication).commentRepository

        setContent {
            AndroidFinalTheme {
                Scaffold { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        userRepository = userRepository,
                        postRepository = postRepository,
                        commentRepository = commentRepository
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    userRepository: com.example.androidfinal.data.repository.UserRepository,
    postRepository: com.example.androidfinal.data.repository.PostRepository,
    commentRepository: com.example.androidfinal.data.repository.CommentRepository
) {
    // Создаем ViewModels с фабриками
    val registrationViewModel: RegistrationViewModel = viewModel(
        factory = RegistrationViewModel.Factory(userRepository)
    )

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory(userRepository)
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(userRepository, postRepository, commentRepository)
    )

    val postViewModel: PostViewModel = viewModel(
        factory = PostViewModel.Factory(postRepository)
    )

    val commentViewModel: CommentViewModel = viewModel(
        factory = CommentViewModel.Factory(commentRepository)
    )

    // Отслеживаем навигационное состояние
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    // Отображаем соответствующий экран
    when (val screen = currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { userId, email, username ->
                    currentScreen = Screen.Home(userId, email, username)
                },
                onNavigateToRegistration = {
                    currentScreen = Screen.Registration
                }
            )
        }
        is Screen.Registration -> {
            RegistrationScreen(
                viewModel = registrationViewModel,
                onRegistrationSuccess = { userId, email, username ->
                    currentScreen = Screen.Home(userId, email, username)
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }
        is Screen.Home -> {
            HomeScreen(
                viewModel = homeViewModel,
                postViewModel = postViewModel,
                commentViewModel = commentViewModel,
                userId = screen.userId,
                userEmail = screen.email,
                onLogout = {
                    currentScreen = Screen.Login
                }
            )
        }
    }
}

// Навигационные состояния
sealed class Screen {
    object Login : Screen()
    object Registration : Screen()
    data class Home(val userId: Long, val email: String, val username: String) : Screen()
}