package com.example.firstapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstapp.data.model.Task
import com.example.firstapp.data.repository.TaskRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = mutableStateOf<List<Task>>(emptyList())
    val tasks: List<Task> get() = _tasks.value

    private val _isSyncing = mutableStateOf(false)
    val isSyncing: Boolean get() = _isSyncing.value

    private val _isTimeout = mutableStateOf(false)
    val isTimeout: Boolean get() = _isTimeout.value

    private var syncTimeoutJob: Job? = null

    companion object {
        private const val TAG = "TaskViewModel"
        private const val SYNC_TIMEOUT = 10000L // 10 secondes
    }

    init {
        Log.d(TAG, "Initialisation du ViewModel")

        viewModelScope.launch(Dispatchers.IO) {
            val cachedTasks = repository.getAllTasks()
            Log.d(TAG, "Cache charge: ${cachedTasks.size} taches")
            withContext(Dispatchers.Main) {
                _tasks.value = cachedTasks
                Log.d(TAG, "UI mise a jour avec ${_tasks.value.size} taches")
            }
        }

        repository.tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "===== FIREBASE ONDATACHANGE DECLENCHE =====")
                val updatedTasks = mutableListOf<Task>()

                for (child in snapshot.children) {
                    child.getValue(Task::class.java)?.let { task ->
                        updatedTasks.add(task)
                        Log.d(TAG, "  -> ${task.id}: ${task.label} | isDone=${task.isDone}")
                    }
                }

                Log.d(TAG, "Total taches: ${updatedTasks.size}")

                viewModelScope.launch(Dispatchers.Main) {
                    _tasks.value = updatedTasks.toList()
                    stopSyncing()
                    Log.d(TAG, "UI mise a jour: ${_tasks.value.size} taches")
                }

                repository.updateCache(updatedTasks)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Erreur Firebase: ${error.message}")
                viewModelScope.launch(Dispatchers.Main) {
                    stopSyncing()
                }
            }
        })

        viewModelScope.launch(Dispatchers.IO) {
            repository.syncPendingTasks()
        }
    }

    private fun startSyncing() {
        _isSyncing.value = true
        _isTimeout.value = false

        syncTimeoutJob?.cancel()

        syncTimeoutJob = viewModelScope.launch {
            delay(SYNC_TIMEOUT)
            Log.w(TAG, "Timeout de synchronisation atteint")
            _isTimeout.value = true
        }
    }

    private fun stopSyncing() {
        syncTimeoutJob?.cancel()
        _isSyncing.value = false
        _isTimeout.value = false
    }

    fun getCompletedTasks(): List<Task> = _tasks.value.filter { it.isDone }

    fun getIncompleteTasks(): List<Task> = _tasks.value.filter { !it.isDone }

    fun addTask(label: String) {
        if (label.isBlank()) {
            Log.w(TAG, "Tentative d'ajout d'une tache vide")
            return
        }

        startSyncing()
        val newTask = Task(label = label.trim(), isDone = false)
        Log.d(TAG, "Ajout tache: $label")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addTask(newTask)
                Log.d(TAG, "Tache ajoutee avec succes: $label")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur ajout tache: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    stopSyncing()
                }
            }
        }
    }

    fun markAsDone(id: String) {
        val currentTask = _tasks.value.find { it.id == id }
        Log.d(TAG, "Toggle etat tache: $id (actuel: ${currentTask?.isDone})")

        startSyncing()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.markTaskAsDone(id)
                Log.d(TAG, "Etat de la tache change: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur changement d'etat: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    stopSyncing()
                }
            }
        }
    }

    fun removeTask(id: String) {
        Log.d(TAG, "Suppression tache: $id")
        startSyncing()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removeTask(id)
                Log.d(TAG, "Tache supprimee: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur suppression: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    stopSyncing()
                }
            }
        }
    }

    fun debugState() {
        Log.d(TAG, "=== DEBUG TASKS ===")
        Log.d(TAG, "Nombre de taches: ${_tasks.value.size}")
        _tasks.value.forEachIndexed { index, task ->
            Log.d(TAG, "$index: ${task.id} - ${task.label} - Done: ${task.isDone}")
        }
        Log.d(TAG, "==================")
    }
}