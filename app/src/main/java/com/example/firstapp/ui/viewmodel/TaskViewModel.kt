package com.example.firstapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstapp.data.model.Task
import com.example.firstapp.data.repository.TaskRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel() : ViewModel() {

    private val repository = TaskRepository();
    // Liste observable pour Compose
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    companion object {
        private const val TAG = "TaskViewModel"
    }

    init {

        // 1. Charger d'abord le cache local pour affichage immédiat
        viewModelScope.launch(Dispatchers.IO) {
            val cachedTasks = repository.getAllTasks()
            Log.d(TAG, "📦 Cache chargé: ${cachedTasks.size} tâches")
            withContext(Dispatchers.Main) {
                _tasks.clear()
                _tasks.addAll(cachedTasks)
                Log.d(TAG, "✅ UI mise à jour avec le cache")
            }
        }

        // 2. Écouter Firebase en temps réel
        repository.tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "🔥 Firebase onDataChange déclenché")
                val updatedTasks = mutableListOf<Task>()

                for (child in snapshot.children) {
                    child.getValue(Task::class.java)?.let { task ->
                        updatedTasks.add(task)
                        Log.d(TAG, "   - Tâche: ${task.id} | ${task.label} | isDone=${task.isDone}")
                    }
                }

                Log.d(TAG, "📊 Total tâches Firebase: ${updatedTasks.size}")

                // IMPORTANT : Mettre à jour sur le Main thread pour déclencher recomposition
                viewModelScope.launch(Dispatchers.Main) {
                    _tasks.clear()
                    _tasks.addAll(updatedTasks)
                    Log.d(TAG, "✅ UI recomposée avec ${_tasks.size} tâches")
                }

                // Mettre à jour le cache local
                repository.updateCache(updatedTasks)
                Log.d(TAG, "✅ Cache local mis à jour")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "❌ Erreur Firebase: ${error.message}")
            }
        })

        // 3. Resynchroniser les modifications en attente
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncPendingTasks()
            Log.d(TAG, "🔄 Synchronisation des tâches en attente effectuée")
        }
    }

    // Tâches complétées
    fun getCompletedTasks(): List<Task> = _tasks.filter { it.isDone }

    // Tâches incomplètes
    fun getIncompleteTasks(): List<Task> = _tasks.filter { !it.isDone }

    // Ajouter une tâche
    fun addTask(label: String) {
        if (label.isBlank()) {
            Log.w(TAG, "⚠️ Tentative d'ajout d'une tâche vide")
            return
        }

        val newTask = Task(label = label.trim(), isDone = false)
        Log.d(TAG, "➕ Ajout tâche: $label")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addTask(newTask)
                Log.d(TAG, "Tâche ajoutée avec succès: $label")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur ajout tâche: ${e.message}", e)
            }
        }
        refreshTasks()

    }

    // Marquer une tâche comme terminée ou non terminée (toggle)
    fun markAsDone(id: String) {
        val currentTask = _tasks.find { it.id == id }
        Log.d(TAG, "✓ Toggle état tâche: $id (actuel: ${currentTask?.isDone})")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.markTaskAsDone(id)
                Log.d(TAG, "✅ État de la tâche changé: $id")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erreur changement d'état: ${e.message}", e)
            }
        }
    }

    // Supprimer une tâche
    fun removeTask(id: String) {
        Log.d(TAG, "🗑️ Suppression tâche: $id")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removeTask(id)
                Log.d(TAG, "✅ Tâche supprimée: $id")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erreur suppression: ${e.message}", e)
            }
        }
    }

    // Fonction de débogage pour vérifier l'état
    fun debugState() {
        Log.d(TAG, "=== DEBUG TASKS ===")
        Log.d(TAG, "Nombre de tâches: ${_tasks.size}")
        _tasks.forEachIndexed { index, task ->
            Log.d(TAG, "$index: ${task.id} - ${task.label} - Done: ${task.isDone}")
        }
        Log.d(TAG, "==================")
    }

    private fun refreshTasks() {
//        repository.getTasks { taskList ->
        _tasks.clear()
//            _tasks.addAll(taskList)
//        }
    }
}