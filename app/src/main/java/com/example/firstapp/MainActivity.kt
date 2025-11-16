package com.example.firstapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.example.firstapp.data.repository.TaskRepository
import com.example.firstapp.ui.screen.TaskScreen
import com.example.firstapp.ui.theme.TodoAppTheme
import com.example.firstapp.ui.viewmodel.TaskViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val databaseUrl = "https://todoapp-de656-default-rtdb.europe-west1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl)

        try {
            database.setPersistenceEnabled(true)
        } catch (e: Exception) {
            Log.d("MainActivity", "Persistance deja activee")
        }

        val repository = TaskRepository(applicationContext)
        val viewModel = TaskViewModel(repository)

        // LOG IMPORTANT
        Log.d("MainActivity", "ViewModel cree: hashCode=${viewModel.hashCode()}")
//        Log.d("MainActivity", "Repository ref: ${repository.tasksRef.path}")

        setContent {
            // LOG pour verifier les recompositions
            LaunchedEffect(Unit) {
                Log.d("MainActivity", "setContent execute")
            }

            TodoAppTheme {
                TaskScreen(viewModel, true)
            }
        }
    }
}