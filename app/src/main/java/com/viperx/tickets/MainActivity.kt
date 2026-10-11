package com.viperx.tickets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AppRoot() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val repo = remember { TicketRepository() }
    var tickets by remember { mutableStateOf<List<Ticket>?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        tickets = repo.getTodaysTickets()
        loading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Viper X Tickets") }) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                Column {
                    Button(
                        onClick = {
                            loading = true
                            scope.launch {
                                tickets = repo.getTodaysTickets()
                                loading = false
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    ) { Text("Refresh Today's Tickets") }

                    LazyColumn {
                        items(tickets ?: emptyList()) { ticket -> TicketCard(ticket) }
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket) {
    Card(Modifier.padding(12.dp).fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(ticket.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Total Odds: %.2f".format(ticket.totalOdds),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text("Estimated chance all legs win: %.2f%%".format(ticket.combinedProbability * 100))
            Divider(Modifier.padding(vertical = 8.dp))
            ticket.legs.forEach { leg ->
                Column(Modifier.padding(vertical = 4.dp)) {
                    Text(leg.matchName, fontWeight = FontWeight.SemiBold)
                    Text("${leg.marketType} → ${leg.selection} @ ${leg.odds}")
                }
            }
        }
    }

}
