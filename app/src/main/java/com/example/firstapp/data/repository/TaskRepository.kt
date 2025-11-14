package com.example.firstapp.data.repository

import android.content.Context
import com.example.firstapp.data.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class TaskRepository(context: Context) {
    private val prefs = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private var currentId = -1

    open fun getAllTasks(): List<Task> {
        val json = prefs.getString("tasks", "[]")
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getCompletedTasks(): List<Task> = getAllTasks().filter { it.isDone }

    fun getIncompleteTasks(): List<Task> = getAllTasks().filter { !it.isDone }

    fun addTask(label: String) {
        val tasks = getAllTasks().toMutableList()
        tasks.add(Task(++currentId, label))
        saveTasks(tasks)
    }

    fun markTaskAsDone(id: Int) {
        val tasks = getAllTasks().toMutableList()
        tasks.find { it.id == id }?.isDone = true
        saveTasks(tasks)
    }

    fun removeTask(id: Int) {
        val tasks = getAllTasks().toMutableList()
        tasks.removeIf { it.id == id }
        saveTasks(tasks)
    }

    private fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString("tasks", json).apply()
    }
}