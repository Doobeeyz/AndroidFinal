package com.example.androidfinal.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Post::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Long,
    val author: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)