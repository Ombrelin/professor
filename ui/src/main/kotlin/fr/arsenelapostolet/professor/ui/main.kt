package fr.arsenelapostolet.professor.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.arsenelapostolet.Library

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Click") }

    MaterialTheme {
        Button(
            onClick = { text = Library().hello() },
            modifier = Modifier.testTag("button")
        ) {
            Text(text)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}