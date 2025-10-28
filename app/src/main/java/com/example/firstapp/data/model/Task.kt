package com.example.firstapp.data.model


data class Task(
    val id: Int,
    val label: String,
    var isDone: Boolean = false
)