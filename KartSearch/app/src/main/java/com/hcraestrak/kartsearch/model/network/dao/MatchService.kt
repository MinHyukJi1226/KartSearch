package com.hcraestrak.kartsearch.model.network.dao

import com.hcraestrak.kartsearch.model.network.data.response.Match
import com.hcraestrak.kartsearch.model.network.data.response.MatchDetailPlayer
import com.hcraestrak.kartsearch.model.network.data.response.MatchDetailTeam
import com.hcraestrak.kartsearch.model.network.data.response.MatchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchService {
    @GET("/users/{access_id}/matches")
    fun accessIdMatchInquiry(
        @Path("access_id") access_id: String,
        @Query("start_date") start_date: String? = null,
        @Query("end_date") end_date: String? = null,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("match_types") match_types: String? = null
    ): Call<MatchResponse>

    @GET("/matches")
    fun allMatchInquiry(
        @Query("start_date") start_date: String? = null,
        @Query("end_date") end_date: String? = null,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("match_types") match_types: String? = null
    ): Call<Match>

    @GET("/matches/{match_id}")
    fun specificMatchInquiry(@Path("match_id") match_id: String): Call<MatchDetailPlayer>

    @GET("/matches/{match_id}")
    fun specificTeamMatchInquiry(@Path("match_id") match_id: String): Call<MatchDetailTeam>

}