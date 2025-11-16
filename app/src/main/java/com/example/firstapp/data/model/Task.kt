package com.example.firstapp.data.model

import com.google.firebase.database.PropertyName



data class Task(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String? = null,

    @get:PropertyName("label")
    @set:PropertyName("label")
    var label: String? = null,

    @get:PropertyName("isDone")
    @set:PropertyName("isDone")
    var isDone: Boolean = false
) {
    constructor() : this(null, null, false)
}