package com.viperx.tickets

import retrofit2.http.GET
import retrofit2.http.Query

interface OddsApi {
    @GET("fixtures")
    suspend fun getFixtures(
        @Query("apiKey") apiKey: String = Config.API_KEY,
        @Query("sportId") sportId: Int,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("hasOdds") hasOdds: Boolean = true
    ): List<Fixture>

    @GET("odds")
    suspend fun getOdds(
        @Query("apiKey") apiKey: String = Config.API_KEY,
        @Query("fixtureId") fixtureId: Long,
        @Query("oddsFormat") oddsFormat: String = "decimal"
    ): OddsResponse

}
