import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstapp.ui.screen.TaskScreen
import com.example.firstapp.ui.theme.TodoAppTheme
import com.example.firstapp.ui.viewmodel.TaskViewModel

@Composable
fun TodoApp(darkTheme: Boolean = false) {
    val backgroundColor = if (darkTheme) Color(0xFF2C2F4A) else Color(0xFFF7F8FA)
    val textColor = if (darkTheme) Color.White else Color.Black
    val cardColor = if (darkTheme) Color(0xFF3A3F6E) else Color.White

    var newTask by remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf(
        "Pick up mail",
        "Buy cat food",
        "Get gift for grandma",
        "Doctors appointment"
    )}
    val completedTasks = remember { mutableStateListOf(
        "Feed dog",
        "Renew registration"
    )}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CHORES", fontWeight = FontWeight.Bold, color = textColor, fontSize = 20.sp)
            IconButton(onClick = { /* TODO: menu */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Item
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = newTask,
                onValueChange = { newTask = it },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(onClick = {
                if (newTask.isNotBlank()) {
                    tasks.add(newTask)
                    newTask = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.Blue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TO DO Section
        Text("TO DO", fontWeight = FontWeight.SemiBold, color = textColor)
        Spacer(modifier = Modifier.height(8.dp))
        tasks.forEach { task ->
            TaskItem(task = task, completed = false, darkTheme = darkTheme)
            Spacer(modifier = Modifier.height(10.dp))

        }

        Spacer(modifier = Modifier.height(16.dp))

        // COMPLETED Section
        Text("COMPLETED", fontWeight = FontWeight.SemiBold, color = textColor)
        Spacer(modifier = Modifier.height(8.dp))
        completedTasks.forEach { task ->
            TaskItem(task = task, completed = true, darkTheme = darkTheme)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TaskItem(task: String, completed: Boolean, darkTheme: Boolean) {
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
        Checkbox(
            checked = completed,
            onCheckedChange = { /* TODO */ }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task,
            color = textColor,
            fontSize = 16.sp,
            style = if (completed) LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else LocalTextStyle.current
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Due today",
            fontSize = 12.sp,
            color = if (darkTheme) Color(0xFFB0B0B0) else Color.Gray
        )
    }
    Spacer(modifier = Modifier.width(8.dp))

}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoAppTheme {
        TodoApp(true)
    }
}