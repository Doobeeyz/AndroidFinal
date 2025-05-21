package com.example.androidfinal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.androidfinal.data.models.User
import com.example.androidfinal.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Состояния для UI
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                // Проверяем существования пользователя с таким email
                val user = userRepository.getUserByEmail(email)

                if (user == null) {
                    _loginState.value = LoginState.Error("Пользователь с таким email не найден")
                    return@launch
                }

                // Проверяем пароль
                val result = BCrypt.verifyer()
                    .verify(password.toCharArray(), user.password.toCharArray())

                if (!result.verified) {
                    _loginState.value = LoginState.Error("Неверный пароль")
                    return@launch
                }

                // Вход успешен
                _loginState.value = LoginState.Success(
                    userId = user.id,
                    email = user.email,
                    username = user.username
                )
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    // Сброс состояния
    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    // Состояния процесса входа
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val userId: Long, val email: String, val username: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    // Factory для создания ViewModel с зависимостями
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}