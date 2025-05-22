package com.example.androidfinal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.data.models.User
import com.example.androidfinal.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import at.favre.lib.crypto.bcrypt.BCrypt

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerUser(username: String, email: String, birthdate: String, password: String) {
        viewModelScope.launch {
            try {
                _registrationState.value = RegistrationState.Loading


                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _registrationState.value = RegistrationState.Error("Пользователь с таким email уже существует")
                    return@launch
                }


                val newUser = User(
                    username = username,
                    email = email,
                    password = BCrypt.withDefaults().hashToString(12, password.toCharArray()),
                    birthDate = birthdate
                )

                val userId = userRepository.insertUser(newUser)

                _registrationState.value = RegistrationState.Success(
                    userId = userId,
                    email = email,
                    username = username
                )
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        data class Success(val userId: Long, val email: String, val username: String) : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
                return RegistrationViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}