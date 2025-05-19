package com.example.androidfinal

import android.app.Application
import androidx.room.Room
import com.example.androidfinal.data.db.AppDatabase
import com.example.androidfinal.data.repository.UserRepository

class MyApplication : Application() {

    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    val userRepository by lazy {
        UserRepository(database.userDao())
    }
}