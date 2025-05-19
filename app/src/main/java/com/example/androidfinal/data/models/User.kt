package com.example.androidfinal.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val email: String,
    val username: String,
    val password: String,
    val birthDate: String,
    val interests: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
