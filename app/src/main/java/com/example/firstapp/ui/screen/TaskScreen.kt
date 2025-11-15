package com.example.firstapp.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firstapp.ui.viewmodel.TaskViewModel
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.firstapp.data.model.Task
import com.example.firstapp.ui.theme.TodoAppTheme

@Composable
fun TaskScreen(viewModel: TaskViewModel, darkTheme: Boolean = false) {

    val backgroundColor = if (darkTheme) Color(0xFF2C2F4A) else Color(0xFFF7F8FA)
    val textColor = if (darkTheme) Color.White else Color.Black
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White

    var newTask by remember { mutableStateOf(TextFieldValue("")) }
    val tasks by remember { derivedStateOf { viewModel.tasks } }
    val incompleteTasks = tasks.filter { !it.isDone }
    val completedTasks = tasks.filter { it.isDone }
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
                Text("To-Do App", fontWeight = FontWeight.Bold, color = textColor, fontSize = 20.sp)
                /*IconButton(onClick = { *//* TODO: menu *//* }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textColor)
                }*/
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
                    cursorBrush = SolidColor(textColor)
                )
                IconButton(onClick = {
                    if (newTask.text.isNotBlank()) {
                        viewModel.addTask(newTask.text)
                        newTask = TextFieldValue("")
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter une Tâche", tint = Color.Blue, modifier = Modifier.background(Color.White))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("TO DO", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Liste des tâches à faire
        items(incompleteTasks.asReversed()) { task ->
            TaskItem(task = task, completed = false, darkTheme = darkTheme, viewModel = viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("COMPLETED", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Liste des tâches complétées
        items(completedTasks.asReversed()) { task ->
            TaskItem(task = task, completed = true, darkTheme = darkTheme, viewModel = viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
//
        /*
        Text(
            text = "ToDo App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text("Nouvelle tâche") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (newTask.text.isNotBlank()) {
                    viewModel.addTask(newTask.text)
                    newTask = TextFieldValue("")
                }
            }) {
                Text("Ajouter")
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (task.isDone) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${task.id} ${task.label}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (!task.isDone) {
                            Button(onClick = { viewModel.markAsDone(task.id) }) {
                                Text("Terminer")
                            }
                        } else {
                            Text("Fait", color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }
}*/


@Composable
fun TaskItem(task: Task, completed: Boolean, darkTheme: Boolean, viewModel: TaskViewModel) {
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White
    val textColor = if (darkTheme) Color.White else Color.Black

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .padding(vertical = 4.dp)

    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable { task.id?.let { viewModel.markAsDone(it) } }

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
                    .padding(3.dp) // Réduire cette valeur pour moins d'espace
                    .background(Color.White, CircleShape)
            )

            // Coche quand checked
            if (task.isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    modifier = Modifier
                        .matchParentSize()
                        .padding(4.dp), // Ajuster pour centrer la coche
                    tint = Color.Blue
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        task.label?.let {
            Text(
                text = it,
                color = textColor,
                fontSize = 16.sp,
                style = if (completed) LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else LocalTextStyle.current
            )
        }

        Spacer(modifier = Modifier.weight(1f))
       /* var text = "A faire"
        if(completed)
        {
            text = "Supprimer"

        }*/
        IconButton(onClick = {

            task.id?.let { viewModel.removeTask(it)}

        }) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Supprimer une Tâche",
                tint = deleteIconColor(true)

            )
        }
       /* Text(
            text = text ,
            fontSize = 12.sp,
            modifier = Modifier.clickable {
                if(completed) {
                    task.id?.let { viewModel.removeTask(it) }
                }
            },
            color = if (darkTheme) Color(0xFFB0B0B0) else Color.Gray
        )*/

    }
    Spacer(modifier = Modifier.width(8.dp))

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
    TodoAppTheme {
        // Utilisation du FakeRepository pour injecter des données factices
        val fakeRepo = com.example.firstapp.data.repository.FakeTaskRepository()
        val fakeViewModel = com.example.firstapp.ui.viewmodel.TaskViewModel(
            object : com.example.firstapp.data.repository.TaskRepository(
                android.content.ContextWrapper(null) // contexte factice
            ) {
                override fun getAllTasks(): List<Task> = fakeRepo.getAllTasks()
            }
        )
        TaskScreen(viewModel = fakeViewModel, darkTheme = true)
    }
}
