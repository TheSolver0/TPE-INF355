package com.example.firstapp.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstapp.data.model.Task
import com.example.firstapp.data.repository.TaskRepository
import com.example.firstapp.ui.theme.TodoAppTheme
import com.example.firstapp.ui.viewmodel.TaskViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel, darkTheme: Boolean = false) {

    val backgroundColor = if (darkTheme) Color(0xFF2C2F4A) else Color(0xFFF7F8FA)
    val textColor = if (darkTheme) Color.White else Color.Black
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White

    var newTask by remember { mutableStateOf(TextFieldValue("")) }

    // CORRECTION : Observer directement tasks au lieu d'utiliser derivedStateOf
    val tasks = viewModel.tasks
    val incompleteTasks = remember(tasks) { tasks.filter { !it.isDone } }
    val completedTasks = remember(tasks) { tasks.filter { it.isDone } }

    // Debug : voir les changements en temps réel
    LaunchedEffect(tasks.size) {
        Log.d("TaskScreen", "🔄 Recomposition - ${tasks.size} tâches")
        Log.d("TaskScreen", "   TO DO: ${incompleteTasks.size}")
        Log.d("TaskScreen", "   COMPLETED: ${completedTasks.size}")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "To-Do App",
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Item
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor, RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(color = textColor, fontSize = 16.sp),
                    cursorBrush = SolidColor(textColor),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(4.dp)) {
                            if (newTask.text.isEmpty()) {
                                Text(
                                    "Ajouter une tâche...",
                                    style = TextStyle(
                                        color = textColor.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                IconButton(onClick = {
                    if (newTask.text.isNotBlank()) {
                        viewModel.addTask(newTask.text)
                        newTask = TextFieldValue("")
                    }
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Ajouter une Tâche",
                        tint = Color.Blue,
                        modifier = Modifier.background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("TO DO", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Liste des tâches à faire
        items(incompleteTasks.asReversed()) { task ->
            TaskItem(
                task = task,
                completed = false,
                darkTheme = darkTheme,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("COMPLETED", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Liste des tâches complétées
        items(completedTasks.asReversed()) { task ->
            TaskItem(
                task = task,
                completed = true,
                darkTheme = darkTheme,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    completed: Boolean,
    darkTheme: Boolean,
    viewModel: TaskViewModel
) {
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White
    val textColor = if (darkTheme) Color.White else Color.Black

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        // Checkbox personnalisée - CORRECTION : vérifier que task.id n'est pas null
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    task.id?.let { id ->
                        viewModel.markAsDone(id)
                    } ?: run {
                        Log.w("TaskItem", "⚠️ Tentative de toggle sur tâche sans ID")
                    }
                }
        ) {
            // Bordure externe
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.5.dp, Color.Gray, CircleShape)
            )

            // Fond blanc intérieur
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(3.dp)
                    .background(Color.White, CircleShape)
            )

            // Coche quand terminé
            if (task.isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Terminé",
                    modifier = Modifier
                        .matchParentSize()
                        .padding(4.dp),
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Texte de la tâche
        task.label?.let {
            Text(
                text = it,
                color = textColor,
                fontSize = 16.sp,
                style = if (completed) {
                    LocalTextStyle.current.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                } else {
                    LocalTextStyle.current
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Bouton Supprimer - CORRECTION : vérifier que task.id n'est pas null
        IconButton(
            onClick = {
                task.id?.let { id ->
                    viewModel.removeTask(id)
                } ?: run {
                    Log.w("TaskItem", "⚠️ Tentative de suppression sur tâche sans ID")
                }
            }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Supprimer une Tâche",
                tint = deleteIconColor(darkTheme)
            )
        }
    }
}

fun deleteIconColor(darkTheme: Boolean): Color {
    return if (darkTheme) {
        Color.hsl(0f, 0.25f, 0.78f) // Rouge clair désaturé
    } else {
        Color.hsl(0f, 0.75f, 0.45f) // Rouge plus vif
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // Pour la preview, on crée un ViewModel simple sans vrai repository
    // Ceci n'est utilisé que pour la preview, pas dans l'app réelle
    TodoAppTheme {
        // Preview avec des données mockées
        PreviewTaskScreen()
    }
}

@Composable
private fun PreviewTaskScreen() {
    val backgroundColor = Color(0xFF2C2F4A)
    val textColor = Color.White
    val cardColor = Color(0xFF3A3F6E)

    // Données de preview mockées
    val previewTasks = remember {
        listOf(
            Task(id = "1", label = "Faire les courses", isDone = false),
            Task(id = "2", label = "Étudier Kotlin", isDone = false),
            Task(id = "3", label = "Lire un livre", isDone = true)
        )
    }

    var newTask by remember { mutableStateOf(TextFieldValue("")) }
    val incompleteTasks = previewTasks.filter { !it.isDone }
    val completedTasks = previewTasks.filter { it.isDone }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "To-Do App",
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor, RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(color = textColor, fontSize = 16.sp),
                    cursorBrush = SolidColor(textColor),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(4.dp)) {
                            if (newTask.text.isEmpty()) {
                                Text(
                                    "Ajouter une tâche...",
                                    style = TextStyle(
                                        color = textColor.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Ajouter",
                    tint = Color.Blue,
                    modifier = Modifier.background(Color.White, CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("TO DO", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(incompleteTasks) { task ->
            PreviewTaskItem(task, false, true)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("COMPLETED", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(completedTasks) { task ->
            PreviewTaskItem(task, true, true)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PreviewTaskItem(task: Task, completed: Boolean, darkTheme: Boolean) {
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White
    val textColor = if (darkTheme) Color.White else Color.Black

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.5.dp, Color.Gray, CircleShape)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(3.dp)
                    .background(Color.White, CircleShape)
            )
            if (task.isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Terminé",
                    modifier = Modifier
                        .matchParentSize()
                        .padding(4.dp),
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        task.label?.let {
            Text(
                text = it,
                color = textColor,
                fontSize = 16.sp,
                style = if (completed) {
                    LocalTextStyle.current.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                } else {
                    LocalTextStyle.current
                },
                modifier = Modifier.weight(1f)
            )
        }

        Icon(
            Icons.Default.Delete,
            contentDescription = "Supprimer",
            tint = deleteIconColor(darkTheme)
        )
    }
}