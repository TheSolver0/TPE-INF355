package com.example.firstapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
            Log.d("MainActivity", "✅ Firebase OK")
        } catch (e: Exception) {
            Log.d("MainActivity", "⚠️ ${e.message}")
        }

        val repository = TaskRepository(applicationContext)
        val viewModel = TaskViewModel(repository)

        Log.d("MainActivity", "📱 ViewModel créé: $viewModel")

        setContent {
            TodoAppTheme {
                TaskScreen(viewModel, true)
            }
        }
    }
}