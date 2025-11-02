package com.example.firstapp.data.repository

import com.example.firstapp.data.model.Task
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


    fun getCompletedTasks(): List<Task> = tasks.filter { it.isDone }

    fun getIncompleteTasks(): List<Task> = tasks.filter { !it.isDone }

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
