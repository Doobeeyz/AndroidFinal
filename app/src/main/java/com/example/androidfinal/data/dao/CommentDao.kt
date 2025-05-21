package com.example.androidfinal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidfinal.data.models.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Query("SELECT * FROM Comment WHERE postId = :postId")
    suspend fun getCommentsByPost(postId: Long): List<Comment>
}