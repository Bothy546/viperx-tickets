package com.viperx.tickets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TicketRepository {

    private fun todayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    suspend fun getTodaysTickets(): List<Ticket> = withContext(Dispatchers.IO) {
        val today = todayDate() // from = to = TODAY ONLY

        val allFixtures = Config.SPORT_IDS.flatMap { sportId ->
            try {
                ApiClient.api.getFixtures(
                    sportId = sportId,
                    from = today,
                    to = today,
                    hasOdds = true
                )
            } catch (e: Exception) {
                emptyList()
            }
        }.take(40)

        val allMatchLegs = allFixtures.map { fixture ->
            async {
                try {
                    val odds = ApiClient.api.getOdds(fixtureId = fixture.fixtureId)
                    MarketSelector.buildLegs(fixture, odds)
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }.awaitAll().filter { it.isNotEmpty() }

        TicketGenerator.generateAllTickets(allMatchLegs)
    }

}
