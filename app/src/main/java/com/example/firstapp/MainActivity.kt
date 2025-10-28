package com.example.firstapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.firstapp.ui.theme.FirstAppTheme

import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.firstapp.data.model.Task
import com.example.firstapp.ui.screen.TaskScreen
import com.example.firstapp.ui.theme.TodoAppTheme
import com.example.firstapp.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = TaskViewModel()

        setContent {
            TodoAppTheme {
                TaskScreen(viewModel, true)
            }
        }
    }
}
/*
@Composable
fun FirstApp() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),

    ) {

        Spacer(modifier = Modifier.height(50.dp))



        TextField(
            value = "",
            onValueChange = { it + 'd' },
            label = { Text("Tapez quelque chose") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()

        )

        /*Text(
            text = "TO DO"
        )*/


        SimpleList("TO DO")
        SimpleList("COMPLETED")
    }



}

@Composable
fun SimpleList(s: String,) {
//    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")

    Column {
        Text(
            s,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 10.dp)

        )

        taches.forEach { item ->
            Card(
                modifier = Modifier
                    //.background(Color.White)
                    .fillMaxWidth()
                    //.border(1.dp, Color.LightGray, CircleShape)
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      //  .background(Color.White)
                ) {
                    var checked by remember { mutableStateOf(false) }
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },

                    )

                    Text(
                        text = item.label,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FirstAppTheme {
        FirstApp()
    }
}
*/