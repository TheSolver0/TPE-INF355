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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TaskRepository {
    private val tasks = mutableListOf<Task>()
//    private var currentId = -1

//    val database = FirebaseDatabase.getInstance()
    private val database = FirebaseDatabase.getInstance("https://todoapp-de656-default-rtdb.europe-west1.firebasedatabase.app")
    private val tasksRef = database.getReference("tasks")

//    fun getAllTasks(): List<Task> = tasks
    fun getTasks(onDataChanged: (List<Task>) -> Unit) {
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Task>()
                for (child in snapshot.children) {
                    val task = child.getValue(Task::class.java)
                    if (task != null) list.add(task)
                }
                onDataChanged(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erreur Firebase: ${error.message}")
            }
        })
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
//    fun addTask(label: String) {
    fun addTask(task: Task) {
        val key = tasksRef.push().key
        if (key != null) {
            task.id = key
            tasksRef.child(key).setValue(task)
        }
    }

    fun markTaskAsDone(id: String) {
        tasksRef.child(id).child("done").setValue(true)
        tasks.find { it.id == id }?.isDone = true
       /* val updates = mapOf<String, Any>(
            "done" to true)
        tasksRef.child(id).updateChildren(updates)
            .addOnSuccessListener {
                // Succès : La tâche a été marquée comme terminée dans Firebase
                println("Tâche $id marquée comme terminée avec succès.")
            }
            .addOnFailureListener { e ->
                // Erreur : La mise à jour a échoué
                println("Erreur lors de la mise à jour de la tâche $id: ${e.message}")
            }*/
    }

    fun removeTask(id: String) {
        tasksRef.child(id).removeValue()
        tasks.removeAll{ it.id == id }
    }
}