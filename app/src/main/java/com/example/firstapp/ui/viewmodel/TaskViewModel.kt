package com.example.firstapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.firstapp.data.model.Task
import com.example.firstapp.data.repository.TaskRepository

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks


   /* init {
        // Données initiales
        repository.addTask("Se réveiller")
        repository.addTask("Se brosser")
        repository.addTask("Faire du sport")
        refreshTasks()
    }*/
   init {
       loadTasks()
   }

    private fun loadTasks() {
        repository.getTasks { taskList ->
            _tasks.clear()
            _tasks.addAll(taskList)
        }
    }

    fun getCompletedTasks(): List<Task> {
        return repository.getCompletedTasks()
    }

    fun getIncompleteTasks(): List<Task> {
        return repository.getIncompleteTasks()
    }

    fun addTask(label: String) {
        val newTask = Task(label = label)
        repository.addTask(newTask)
        refreshTasks()
    }

    fun markAsDone(id: String) {
        repository.markTaskAsDone(id)
        refreshTasks()
    }

    fun removeTask(id: String) {
        repository.removeTask(id)
        refreshTasks()
    }

    private fun refreshTasks() {
//        repository.getTasks { taskList ->
            _tasks.clear()
//            _tasks.addAll(taskList)
//        }
    }
}