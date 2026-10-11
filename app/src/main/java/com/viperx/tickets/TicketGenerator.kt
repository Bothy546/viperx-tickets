package com.viperx.tickets

object TicketGenerator {
    fun generateAllTickets(allMatchLegs: List<List<Leg>>): List<Ticket> {
        val safePicks = allMatchLegs.mapNotNull { MarketSelector.safestLeg(it) }
            .sortedByDescending { it.impliedProbability }

        return listOf(
            build("Safe (5+)", 5.0, safePicks, allMatchLegs),
            build("Big (50+)", 50.0, safePicks, allMatchLegs),
            build("Mega (280+)", 280.0, safePicks, allMatchLegs)
        )
    }

    private fun build(
        name: String,
        target: Double,
        safePicksSorted: List<Leg>,
        allMatchLegs: List<List<Leg>>
    ): Ticket {
        val chosen = mutableListOf<Leg>()
        var runningOdds = 1.0
        val usedMatches = mutableSetOf<String>()

        for (leg in safePicksSorted) {
            if (runningOdds >= target) break
            chosen.add(leg)
            usedMatches.add(leg.matchName)
            runningOdds *= leg.odds
        }

        if (runningOdds < target) {
            var i = 0
            while (runningOdds < target && i < chosen.size) {
                val leg = chosen[i]
                val matchLegs = allMatchLegs.firstOrNull { it.any { l -> l.matchName == leg.matchName } }
                val better = matchLegs?.let { MarketSelector.allSortedByOdds(it) }
                    ?.firstOrNull { it.odds > leg.odds }
                if (better != null) {
                    runningOdds /= leg.odds
                    runningOdds *= better.odds
                    chosen[i] = better
                } else {
                    i++
                }
            }
        }

        if (runningOdds < target) {
            for (matchLegs in allMatchLegs) {
                if (runningOdds >= target) break
                val matchName = matchLegs.firstOrNull()?.matchName ?: continue
                if (usedMatches.contains(matchName)) continue
                val best = MarketSelector.allSortedByOdds(matchLegs).firstOrNull() ?: continue
                chosen.add(best)
                usedMatches.add(matchName)
                runningOdds *= best.odds
            }
        }

        return Ticket(name, target, chosen)
    }

}
