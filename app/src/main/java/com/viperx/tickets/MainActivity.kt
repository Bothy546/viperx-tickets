package com.viperx.tickets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { DebugScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen() {
    var output by remember { mutableStateOf("Tap the button below.") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("DEBUG MODE") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Button(onClick = {
                loading = true
                output = "Loading..."
                scope.launch {
                    output = withContext(Dispatchers.IO) {
                        try {
                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                            val url = "${Config.BASE_URL}fixtures?apiKey=${Config.API_KEY}&sportId=10&from=$today&to=$today&hasOdds=true"
                            val client = okhttp3.OkHttpClient()
                            val req = okhttp3.Request.Builder().url(url).build()
                            val resp = client.newCall(req).execute()
                            val body = resp.body?.string() ?: "NULL BODY"
                            "CODE: ${resp.code}\n\nBODY:\n${body.take(3000)}"
                        } catch (e: Exception) {
                            "ERROR: ${e.message}"
                        }
                    }
                    loading = false
                }
            }) {
                Text(if (loading) "Loading..." else "Test API Call")
            }

            Spacer(Modifier.height(16.dp))

            Text(output, modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()))
        }
    }

}
