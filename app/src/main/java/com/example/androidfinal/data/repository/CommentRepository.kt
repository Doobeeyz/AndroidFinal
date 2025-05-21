package com.example.androidfinal.data.repository

import com.example.androidfinal.data.dao.CommentDao
import com.example.androidfinal.data.models.Comment

class CommentRepository(private val commentDao: CommentDao) {
    suspend fun insertComment(comment: Comment) = commentDao.insertComment(comment)
    suspend fun getCommentsByPost(postId: Long) = commentDao.getCommentsByPost(postId)
}