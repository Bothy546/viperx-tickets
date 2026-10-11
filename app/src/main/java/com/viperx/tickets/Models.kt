package com.viperx.tickets

data class Fixture(
    val fixtureId: Long,
    val homeTeam: String?,
    val awayTeam: String?,
    val startTime: String?
)

data class OddsResponse(
    val fixtureId: Long?,
    val markets: List<MarketGroup>?
)

data class MarketGroup(
    val marketName: String?,
    val selections: List<Selection>?
)

data class Selection(
    val name: String?,
    val odds: Double?,
    val point: Double? = null
)

data class Leg(
    val matchName: String,
    val marketType: String,
    val selection: String,
    val odds: Double,
    val impliedProbability: Double,
    val point: Double? = null
)

data class Ticket(
    val name: String,
    val targetOdds: Double,
    val legs: List<Leg>
) {
    val totalOdds: Double get() = legs.fold(1.0) { acc, l -> acc * l.odds }
    val combinedProbability: Double get() = legs.fold(1.0) { acc, l -> acc * l.impliedProbability }

}
