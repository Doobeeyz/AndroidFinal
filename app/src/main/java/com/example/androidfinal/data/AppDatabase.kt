package com.example.androidfinal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidfinal.data.dao.CommentDao
import com.example.androidfinal.data.dao.PostDao
import com.example.androidfinal.data.dao.UserDao
import com.example.androidfinal.data.models.Comment
import com.example.androidfinal.data.models.Post
import com.example.androidfinal.data.models.User

@Database(entities = [User::class, Post::class, Comment::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}
