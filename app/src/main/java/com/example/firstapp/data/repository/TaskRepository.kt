package com.example.firstapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.firstapp.data.model.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await

class TaskRepository(context: Context) {

    // CORRECTION : Utiliser l'instance Firebase avec l'URL Europe-West1
    private val database = FirebaseDatabase.getInstance("https://todoapp-de656-default-rtdb.europe-west1.firebasedatabase.app")
    val tasksRef: DatabaseReference = database.reference.child("tasks")

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TAG = "TaskRepository"
        private const val CACHE_KEY = "tasks_cache"
        private const val PENDING_SYNC_KEY = "pending_sync"
        private const val LAST_SYNC_KEY = "last_sync_timestamp"
    }

    init {
        Log.d(TAG, "Repository initialisé")
//        Log.d(TAG, "Firebase path: ${tasksRef.path}")
    }

    // ========== RÉCUPÉRATION DES TÂCHES ==========

    suspend fun getAllTasks(): List<Task> {
        val cached = getCachedTasks()
        Log.d(TAG, "getAllTasks() - Cache contient: ${cached.size} tâches")
        return cached
    }

    // ========== AJOUT DE TÂCHE ==========

    suspend fun addTask(task: Task) {
        Log.d(TAG, "addTask() appelé: ${task.label}")
//        kotlinx.coroutines.delay(2000)
        // Générer un ID si absent
        val taskId = task.id ?: tasksRef.push().key
        if (taskId == null) {
            Log.e(TAG, "Impossible de générer un ID Firebase")
            return
        }

        val taskWithId = task.copy(id = taskId)
        Log.d(TAG, "ID généré: $taskId")

        try {
            // 1. Créer un Map pour forcer la structure
            val taskMap = hashMapOf<String, Any>(
                "id" to taskId,
                "label" to (taskWithId.label ?: ""),
                "isDone" to taskWithId.isDone
            )

            // Sauvegarde Firebase avec la structure exacte
            tasksRef.child(taskId).setValue(taskMap).await()
            Log.d(TAG, "Tâche sauvegardée dans Firebase: $taskId")

            // 2. Mise à jour du cache local
            addTaskToCache(taskWithId)
            Log.d(TAG, "Tâche ajoutée au cache local")

            removePendingSync(taskId)
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'ajout Firebase: ${e.message}", e)
            // En cas d'échec, sauvegarder localement et marquer pour sync
            addTaskToCache(taskWithId)
            addPendingSync(taskId)
        }
    }

    // ========== MARQUAGE COMME TERMINÉ/NON TERMINÉ ==========

    suspend fun markTaskAsDone(taskId: String) {
        Log.d(TAG, "markTaskAsDone() appelé: $taskId")
//        kotlinx.coroutines.delay(2000)
        try {
            // Récupérer l'état actuel de la tâche
            val currentTask = getCachedTasks().find { it.id == taskId }
            val newIsDoneState = !(currentTask?.isDone ?: false)

            Log.d(TAG, "Changement d'état: ${currentTask?.isDone} → $newIsDoneState")

            // 1. Mise à jour Firebase
            tasksRef.child(taskId).child("isDone").setValue(newIsDoneState).await()
            Log.d(TAG, "isDone=$newIsDoneState dans Firebase: $taskId")

            // 2. Mise à jour du cache
            updateTaskInCache(taskId) { it.copy(isDone = newIsDoneState) }
            Log.d(TAG, "Cache local mis à jour")

            removePendingSync(taskId)
        } catch (e: Exception) {
            Log.e(TAG, "Erreur marquage terminé: ${e.message}", e)
            // Inverser quand même localement en cas d'erreur réseau
            val currentTask = getCachedTasks().find { it.id == taskId }
            val newIsDoneState = !(currentTask?.isDone ?: false)
            updateTaskInCache(taskId) { it.copy(isDone = newIsDoneState) }
            addPendingSync(taskId)
        }
    }

    // ========== SUPPRESSION DE TÂCHE ==========

    suspend fun removeTask(taskId: String) {
        Log.d(TAG, "removeTask() appelé: $taskId")

        try {
            // 1. Suppression Firebase
            tasksRef.child(taskId).removeValue().await()
            Log.d(TAG, "Tâche supprimée de Firebase: $taskId")

            // 2. Suppression du cache
            removeTaskFromCache(taskId)
            Log.d(TAG, "Tâche supprimée du cache")

            removePendingSync(taskId)
        } catch (e: Exception) {
            Log.e(TAG, "Erreur suppression: ${e.message}", e)
            removeTaskFromCache(taskId)
            addPendingSync(taskId)
        }
    }

    // ========== GESTION DU CACHE LOCAL ==========

    private fun getCachedTasks(): List<Task> {
        val json = sharedPrefs.getString(CACHE_KEY, null)
        if (json == null) {
            Log.d(TAG, "Cache vide")
            return emptyList()
        }

        return try {
            val type = object : TypeToken<List<Task>>() {}.type
            val tasks: List<Task> = gson.fromJson(json, type)
            Log.d(TAG, "Cache chargé: ${tasks.size} tâches")
            tasks
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lecture cache: ${e.message}", e)
            emptyList()
        }
    }

    private fun saveCachedTasks(tasks: List<Task>) {
        try {
            val json = gson.toJson(tasks)
            sharedPrefs.edit().putString(CACHE_KEY, json).apply()
            Log.d(TAG, "Cache sauvegardé: ${tasks.size} tâches")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur sauvegarde cache: ${e.message}", e)
        }
    }

    private fun addTaskToCache(task: Task) {
        val tasks = getCachedTasks().toMutableList()
        // Éviter les doublons
        tasks.removeAll { it.id == task.id }
        tasks.add(task)
        saveCachedTasks(tasks)
    }

    private fun updateTaskInCache(taskId: String, transform: (Task) -> Task) {
        val tasks = getCachedTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            tasks[index] = transform(tasks[index])
            saveCachedTasks(tasks)
            Log.d(TAG, "Tâche mise à jour dans le cache: $taskId")
        } else {
            Log.w(TAG, "Tâche non trouvée dans le cache: $taskId")
        }
    }

    private fun removeTaskFromCache(taskId: String) {
        val tasks = getCachedTasks().toMutableList()
        val removed = tasks.removeAll { it.id == taskId }
        if (removed) {
            saveCachedTasks(tasks)
            Log.d(TAG, "Tâche supprimée du cache: $taskId")
        } else {
            Log.w(TAG, "Tâche non trouvée dans le cache: $taskId")
        }
    }

    fun updateCache(tasks: List<Task>) {
        Log.d(TAG, "updateCache() appelé avec ${tasks.size} tâches")
        saveCachedTasks(tasks)
        updateLastSyncTimestamp()
    }

    // ========== GESTION DE LA SYNCHRONISATION ==========

    private fun addPendingSync(taskId: String) {
        val pending = getPendingSyncIds().toMutableSet()
        pending.add(taskId)
        sharedPrefs.edit().putStringSet(PENDING_SYNC_KEY, pending).apply()
        Log.d(TAG, "Tâche ajoutée à la file de sync: $taskId")
    }

    private fun removePendingSync(taskId: String) {
        val pending = getPendingSyncIds().toMutableSet()
        pending.remove(taskId)
        sharedPrefs.edit().putStringSet(PENDING_SYNC_KEY, pending).apply()
    }

    private fun getPendingSyncIds(): Set<String> {
        return sharedPrefs.getStringSet(PENDING_SYNC_KEY, emptySet()) ?: emptySet()
    }

    suspend fun syncPendingTasks() {
        val pendingIds = getPendingSyncIds()
        Log.d(TAG, "syncPendingTasks(): ${pendingIds.size} tâches en attente")

        val cachedTasks = getCachedTasks()

        pendingIds.forEach { taskId ->
            val task = cachedTasks.find { it.id == taskId }
            if (task != null) {
                try {
                    // Forcer la structure correcte
                    val taskMap = hashMapOf<String, Any>(
                        "id" to taskId,
                        "label" to (task.label ?: ""),
                        "isDone" to task.isDone
                    )
                    tasksRef.child(taskId).setValue(taskMap).await()
                    removePendingSync(taskId)
                    Log.d(TAG, "Resync réussie pour: $taskId")
                } catch (e: Exception) {
                    Log.e(TAG, "Échec resync pour $taskId: ${e.message}")
                }
            }
        }
    }

    private fun updateLastSyncTimestamp() {
        sharedPrefs.edit()
            .putLong(LAST_SYNC_KEY, System.currentTimeMillis())
            .apply()
    }

    fun getLastSyncTimestamp(): Long {
        return sharedPrefs.getLong(LAST_SYNC_KEY, 0L)
    }

    fun clearCache() {
        sharedPrefs.edit()
            .remove(CACHE_KEY)
            .remove(PENDING_SYNC_KEY)
            .remove(LAST_SYNC_KEY)
            .apply()
        Log.d(TAG, "Cache nettoyé")
    }

    // ========== DÉBOGAGE ==========

    fun debugState() {
        Log.d(TAG, "=== DEBUG REPOSITORY ===")
//        Log.d(TAG, "Firebase path: ${tasksRef.path}")
        Log.d(TAG, "Cache size: ${getCachedTasks().size}")
        Log.d(TAG, "Pending sync: ${getPendingSyncIds()}")
        Log.d(TAG, "Last sync: ${getLastSyncTimestamp()}")
        Log.d(TAG, "========================")
    }
}