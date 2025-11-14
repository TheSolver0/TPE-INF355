package com.example.firstapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.firstapp.data.model.Task
import com.example.firstapp.data.repository.TaskRepository

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    init {
        refreshTasks()
        if (_tasks.isEmpty()) {
            repository.addTask("Se réveiller")
            repository.addTask("Se brosser")
            repository.addTask("Faire du sport")
            refreshTasks()
        }
    }

    fun getCompletedTasks(): List<Task> = repository.getCompletedTasks()

    fun getIncompleteTasks(): List<Task> = repository.getIncompleteTasks()

    fun addTask(label: String) {
        repository.addTask(label)
        refreshTasks()
    }

    fun markAsDone(id: Int) {
        repository.markTaskAsDone(id)
        refreshTasks()
    }

    fun removeTask(id: Int) {
        repository.removeTask(id)
        refreshTasks()
    }

    private fun refreshTasks() {
        _tasks.clear()
        _tasks.addAll(repository.getAllTasks())
    }
}