package com.example.androidfinal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.data.models.Comment
import com.example.androidfinal.data.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(private val commentRepository: CommentRepository) : ViewModel() {


    private val _commentStates = MutableStateFlow<Map<Long, CommentState>>(emptyMap())
    val commentStates: StateFlow<Map<Long, CommentState>> = _commentStates

    fun getCommentsForPost(postId: Long): CommentState {
        return _commentStates.value[postId] ?: CommentState.Idle
    }

    fun loadComments(postId: Long) {
        viewModelScope.launch {
            updateCommentState(postId, CommentState.Loading)
            try {
                val comments = commentRepository.getCommentsByPost(postId)
                updateCommentState(postId, CommentState.Success(comments))
            } catch (e: Exception) {
                updateCommentState(postId, CommentState.Error(e.message ?: "Ошибка загрузки комментариев"))
            }
        }
    }

    fun addComment(postId: Long, author: String, text: String) {
        viewModelScope.launch {
            updateCommentState(postId, CommentState.Loading)
            try {
                commentRepository.insertComment(Comment(postId = postId, author = author, text = text))
                loadComments(postId)
            } catch (e: Exception) {
                updateCommentState(postId, CommentState.Error(e.message ?: "Ошибка добавления комментария"))
            }
        }
    }

    private fun updateCommentState(postId: Long, newState: CommentState) {
        val currentStates = _commentStates.value.toMutableMap()
        currentStates[postId] = newState
        _commentStates.value = currentStates
    }

    fun resetState(postId: Long) {
        updateCommentState(postId, CommentState.Idle)
    }

    sealed class CommentState {
        object Idle : CommentState()
        object Loading : CommentState()
        data class Success(val comments: List<Comment>) : CommentState()
        data class Error(val message: String) : CommentState()
    }

    class Factory(private val commentRepository: CommentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
                return CommentViewModel(commentRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}