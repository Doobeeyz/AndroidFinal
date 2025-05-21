package com.example.androidfinal.data.repository

import com.example.androidfinal.data.dao.PostDao
import com.example.androidfinal.data.models.Post

class PostRepository(private val postDao: PostDao) {
    suspend fun insertPost(post: Post) = postDao.insertPost(post)
    suspend fun getPostsByUser(userId: Long) = postDao.getPostsByUser(userId)
    suspend fun getAllPosts(): List<Post> = postDao.getAllPosts()

}