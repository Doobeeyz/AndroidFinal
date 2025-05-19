package com.example.androidfinal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidfinal.data.dao.UserDao
import com.example.androidfinal.data.models.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
