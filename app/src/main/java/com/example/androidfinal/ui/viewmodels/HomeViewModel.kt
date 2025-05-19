package com.example.androidfinal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.data.models.User
import com.example.androidfinal.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Состояние данных пользователя
    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState

    // Загрузка данных пользователя по ID
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

    // Загрузка данных пользователя по email
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

    // Состояния получения данных пользователя
    sealed class UserState {
        object Loading : UserState()
        data class Success(val user: User) : UserState()
        data class Error(val message: String) : UserState()
    }

    // Factory для создания ViewModel с зависимостями
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}