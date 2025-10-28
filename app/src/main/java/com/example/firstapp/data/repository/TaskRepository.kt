package com.example.firstapp.data.repository

import com.example.firstapp.data.model.Task


class TaskRepository {
    private val tasks = mutableListOf<Task>()
    private var currentId = -1

    fun getAllTasks(): List<Task> = tasks

    fun getCompletedTasks(): List<Task> = tasks.filter { it.isDone }

    fun getIncompleteTasks(): List<Task> = tasks.filter { !it.isDone }

    fun addTask(label: String) {
        tasks.add(Task(++currentId, label))
    }

    fun markTaskAsDone(id: Int) {
        tasks.find { it.id == id }?.isDone = true
    }

    fun removeTask(id: Int) {
        tasks.removeAt(id)
    }
}
