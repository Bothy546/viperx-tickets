package com.viperx.tickets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var output by remember { mutableStateOf("Tap the button to test the API.") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Viper X - DEBUG MODE") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Button(onClick = {
                loading = true
                output = "Loading..."
                scope.launch {
                    output = withContext(Dispatchers.IO) {
                        try {
                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                            val sb = StringBuilder()
                            sb.append("TODAY = $today\n\n")

                            // Raw call 1: fixtures
                            val fixturesUrl = "${Config.BASE_URL}fixtures?apiKey=${Config.API_KEY}&sportId=10&from=$today&to=$today&hasOdds=true"
                            sb.append("FIXTURES URL:\n$fixturesUrl\n\n")

                            val client = okhttp3.OkHttpClient()
                            val req1 = okhttp3.Request.Builder().url(fixturesUrl).build()
                            val resp1 = client.newCall(req1).execute()
                            val body1 = resp1.body?.string() ?: "NULL BODY"
                            sb.append("FIXTURES RESPONSE CODE: ${resp1.code}\n")
                            sb.append("FIXTURES RAW BODY:\n${body1.take(2000)}\n\n")

                            sb.toString()
                        } catch (e: Exception) {
                            "ERROR: ${e.message}\n${e.stackTraceToString()}"
                        }
                    }
                    loading = false
                }
            }) {
                Text(if (loading) "Loading..." else "Test Fixtures API Call")
            }

            Spacer(Modifier.height(16.dp))

            Text(
                output,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        }
    }

}
