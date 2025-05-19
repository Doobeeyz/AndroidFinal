package com.example.androidfinal.data.repository

import com.example.androidfinal.data.dao.UserDao
import com.example.androidfinal.data.models.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
}