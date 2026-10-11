package com.viperx.tickets

object MarketSelector {
    fun buildLegs(fixture: Fixture, odds: OddsResponse): List<Leg> {
        val markets = odds.markets ?: return emptyList()
        val matchName = "${fixture.participant1Name ?: "Team A"} vs ${fixture.participant2Name ?: "Team B"}"
        val legs = mutableListOf<Leg>()

        for (market in markets) {
            val selections = market.selections?.filterNotNull() ?: continue
            val valid = selections.filter { it.odds != null && it.odds!! > 1.05 }
            if (valid.isEmpty()) continue

            val rawProbs = valid.map { 1.0 / it.odds!! }
            val overround = rawProbs.sum()

            valid.forEachIndexed { i, sel ->
                legs.add(
                    Leg(
                        matchName = matchName,
                        marketType = market.marketName ?: "Unknown",
                        selection = sel.name ?: "Selection",
                        odds = sel.odds!!,
                        impliedProbability = rawProbs[i] / overround,
                        point = sel.point
                    )
                )
            }
        }
        return legs
    }

    fun safestLeg(legs: List<Leg>): Leg? =
        legs.filter { it.odds in 1.05..3.0 }.maxByOrNull { it.impliedProbability }
            ?: legs.minByOrNull { it.odds }

    fun allSortedByOdds(legs: List<Leg>): List<Leg> =
        legs.sortedByDescending { it.odds }

}
