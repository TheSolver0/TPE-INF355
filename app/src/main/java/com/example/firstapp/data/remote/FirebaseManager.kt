package com.example.firstapp.data.remote

import com.example.firstapp.data.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseManager {
    private val db = FirebaseDatabase.getInstance().reference.child("tasks")

    fun listenTasks(onChange: (List<Task>) -> Unit) {
        db.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
                onChange(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveTask(task: Task) {
        task.id?.let { db.child(it).setValue(task) }
    }
}