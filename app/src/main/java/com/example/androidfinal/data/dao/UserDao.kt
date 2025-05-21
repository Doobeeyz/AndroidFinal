package com.example.androidfinal.data.dao

import androidx.room.*
import com.example.androidfinal.data.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM User")
    fun getAllUsers(): Flow<List<User>>
}

