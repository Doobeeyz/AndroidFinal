package com.example.androidfinal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidfinal.data.models.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Query("SELECT * FROM Post WHERE userId = :userId")
    suspend fun getPostsByUser(userId: Long): List<Post>

    @Query("SELECT * FROM Post ORDER BY createdAt DESC")
    suspend fun getAllPosts(): List<Post>
}
