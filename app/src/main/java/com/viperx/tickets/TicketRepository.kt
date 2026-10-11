package com.viperx.tickets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TicketRepository {

    private fun dateOffset(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    suspend fun getTodaysTickets(): List<Ticket> = withContext(Dispatchers.IO) {
        val from = dateOffset(0)
        val to = dateOffset(1) // covers timezone edge cases automatically

        val allFixtures = Config.SPORT_IDS.flatMap { sportId ->
            try {
                ApiClient.api.getFixtures(sportId = sportId, from = from, to = to)
            } catch (e: Exception) {
                emptyList()
            }
        }
            // Only matches that haven't started/finished yet, and that have odds
            .filter { it.hasOdds == true }
            .filter {
                val s = it.statusName?.lowercase() ?: ""
                !s.contains("finish") && !s.contains("cancel") && !s.contains("postpon")
            }
            .take(40)

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
