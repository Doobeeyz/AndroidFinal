package com.example.androidfinal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.data.models.User
import com.example.androidfinal.data.repository.CommentRepository
import com.example.androidfinal.data.repository.PostRepository
import com.example.androidfinal.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
private val userRepository: UserRepository,
private val postRepository: PostRepository,
private val commentRepository: CommentRepository,
) : ViewModel() {


    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState
    private val _userNamesCache = mutableMapOf<Long, String>()


    private val _postState = MutableStateFlow<PostState>(PostState.Loading)
    val postState: StateFlow<PostState> = _postState


    fun loadAllPosts() {
        viewModelScope.launch {
            try {
                _postState.value = PostState.Loading
                val posts = postRepository.getAllPosts()

                if (posts.isNotEmpty()) {
                    _postState.value = PostState.Success(posts)
                } else {
                    _postState.value = PostState.Empty
                }
            } catch (e: Exception) {
                _postState.value = PostState.Error(e.message ?: "Ошибка загрузки постов")
            }
        }
    }


    fun loadUserById(userId: Long) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val user = userRepository.getUserById(userId)

                if (user != null) {
                    _userState.value = UserState.Success(user)
                } else {
                    _userState.value = UserState.Error("Пользователь не найден")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
    suspend fun getUserName(userId: Long): String {
        _userNamesCache[userId]?.let { return it }

        return try {
            val user = userRepository.getUserById(userId)
            val userName = user?.username ?: "Неизвестный пользователь"
            _userNamesCache[userId] = userName
            userName
        } catch (e: Exception) {
            "Неизвестный пользователь"
        }
    }

    fun resetState() {
        _userState.value = UserState.Idle
    }


    fun loadUserByEmail(email: String) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val user = userRepository.getUserByEmail(email)

                if (user != null) {
                    _userState.value = UserState.Success(user)
                } else {
                    _userState.value = UserState.Error("Пользователь не найден")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun getPostsByUser(userId: Long): List<com.example.androidfinal.data.models.Post> {
        val state = _postState.value
        return if (state is PostState.Success) {
            state.posts.filter { it.userId == userId }
        } else {
            emptyList()
        }
    }



    sealed class UserState {
        object Idle : UserState()
        object Loading : UserState()
        data class Success(val user: User) : UserState()
        data class Error(val message: String) : UserState()
    }


    sealed class PostState {
        object Loading : PostState()
        object Empty : PostState()
        data class Success(val posts: List<com.example.androidfinal.data.models.Post>) : PostState()
        data class Error(val message: String) : PostState()
    }


    class Factory(
        private val userRepository: UserRepository,
        private val postRepository: PostRepository,
        private val commentRepository: CommentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(userRepository, postRepository, commentRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
