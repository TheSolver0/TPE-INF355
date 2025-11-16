package com.example.firstapp.data.model


data class Task(
    var id: String? = null,
    var label: String? = null,
    var isDone: Boolean = false
){
    constructor() : this(null, null, false)
}