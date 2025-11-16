package com.example.firstapp.ui.screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstapp.data.model.Task
import com.example.firstapp.ui.viewmodel.TaskViewModel
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.animation.AnimatedVisibility as AnimatedVisibility1

@Composable
fun TaskScreen(viewModel: TaskViewModel, darkTheme: Boolean = false) {

    val backgroundColor = if (darkTheme) Color(0xFF2C2F4A) else Color(0xFFF7F8FA)
    val textColor = if (darkTheme) Color.White else Color.Black
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White

    var newTask by remember { mutableStateOf(TextFieldValue("")) }

    val tasks = viewModel.tasks
    val isSyncing = viewModel.isSyncing
    val isTimeout = viewModel.isTimeout
    val incompleteTasks = tasks.filter { !it.isDone }
    val completedTasks = tasks.filter { it.isDone }

    LaunchedEffect(tasks.size, incompleteTasks.size, completedTasks.size) {
        Log.d("TaskScreen", "Recomposition detectee")
        Log.d("TaskScreen", "Total: ${tasks.size}")
        Log.d("TaskScreen", "TO DO: ${incompleteTasks.size}")
        Log.d("TaskScreen", "COMPLETED: ${completedTasks.size}")
    }

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

                // Indicateur de synchronisation
                AnimatedVisibility1(
                    visible = isSyncing,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    SyncIndicator(isTimeout = isTimeout)
                }
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
                                    "Ajouter une tache...",
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
                        contentDescription = "Ajouter",
                        tint = Color.Blue,
                        modifier = Modifier.background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("TO DO", fontWeight = FontWeight.SemiBold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            items = incompleteTasks.asReversed(),
            key = { it.id ?: it.hashCode() }
        ) { task ->
            AnimatedTaskItem(
                task = task,
                completed = task.isDone,
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

        items(
            items = completedTasks.asReversed(),
            key = { it.id ?: it.hashCode() }
        ) { task ->
            AnimatedTaskItem(
                task = task,
                completed = task.isDone,
                darkTheme = darkTheme,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SyncIndicator(isTimeout: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val color = if (isTimeout) Color(0xFFFF9800) else Color(0xFF6200EE)
    val text = if (isTimeout) "En attente" else "Synchro"

    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotation),
                strokeWidth = 2.5.dp,
                color = color.copy(alpha = alpha)
            )
            Text(
                text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = color.copy(alpha = alpha)
            )
        }
    }
}

@Composable
fun AnimatedTaskItem(
    task: Task,
    completed: Boolean,
    darkTheme: Boolean,
    viewModel: TaskViewModel
) {
    AnimatedVisibility1(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -40 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -40 }
        ) + fadeOut()
    ) {
        TaskItem(
            task = task,
            completed = completed,
            darkTheme = darkTheme,
            viewModel = viewModel
        )
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
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .background(cardColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    task.id?.let { id ->
                        Log.d("TaskItem", "Click sur tache: $id (isDone=${task.isDone})")
                        viewModel.markAsDone(id)
                    }
                }
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

            this@Row.AnimatedVisibility1(
                visible = task.isDone,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Termine",
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
                style = if (task.isDone) {
                    LocalTextStyle.current.copy(
                        textDecoration = TextDecoration.LineThrough
                    )
                } else {
                    LocalTextStyle.current
                },
                modifier = Modifier.weight(1f)
            )
        }

        IconButton(
            onClick = {
                task.id?.let { viewModel.removeTask(it) }
            }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Supprimer",
                tint = if (darkTheme) Color.hsl(0f, 0.25f, 0.78f) else Color.hsl(0f, 0.75f, 0.45f)
            )
        }
    }
}