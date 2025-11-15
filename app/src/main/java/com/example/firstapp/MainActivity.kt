package com.example.firstapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.firstapp.data.repository.TaskRepository
import com.example.firstapp.ui.screen.TaskScreen
import com.example.firstapp.ui.theme.TodoAppTheme
import com.example.firstapp.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = TaskRepository(applicationContext)
        val viewModel = TaskViewModel(repository)

        setContent {
            TodoAppTheme {
                TaskScreen(viewModel, true)
            }
        }
    }
}