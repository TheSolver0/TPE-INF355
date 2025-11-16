package com.example.firstapp.data.local

import android.content.Context

class SharedPrefManager(context: Context) {
    private val prefs = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)

    fun saveTasks(json: String) {
        prefs.edit().putString("tasks_json", json).apply()
    }

    fun getTasks(): String? {
        return prefs.getString("tasks_json", null)
    }
}
