package com.example.androidfinal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.data.models.Post
import com.example.androidfinal.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {

    private val _postState = MutableStateFlow<PostState>(PostState.Idle)
    val postState: StateFlow<PostState> = _postState


    fun loadAllPosts() {
        viewModelScope.launch {
            _postState.value = PostState.Loading
            try {
                val posts = postRepository.getAllPosts()
                _postState.value = PostState.Success(posts)
            } catch (e: Exception) {
                _postState.value = PostState.Error("Не удалось загрузить посты: ${e.message}")
            }
        }
    }


    fun loadPosts(userId: Long) {
        viewModelScope.launch {
            _postState.value = PostState.Loading
            try {
                val posts = postRepository.getPostsByUser(userId)
                _postState.value = PostState.Success(posts)
            } catch (e: Exception) {
                _postState.value = PostState.Error(e.message ?: "Ошибка загрузки постов")
            }
        }
    }

    fun createPost(userId: Long, title: String, content: String) {
        viewModelScope.launch {
            _postState.value = PostState.Loading
            try {
                postRepository.insertPost(Post(userId = userId, title = title, content = content))
                loadPosts(userId)
            } catch (e: Exception) {
                _postState.value = PostState.Error(e.message ?: "Ошибка при создании поста")
            }
        }
    }

    fun resetState() {
        _postState.value = PostState.Idle
    }

    sealed class PostState {
        object Idle : PostState()
        object Loading : PostState()
        data class Success(val posts: List<Post>) : PostState()
        data class Error(val message: String) : PostState()
    }

    class Factory(private val postRepository: PostRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
                return PostViewModel(postRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
