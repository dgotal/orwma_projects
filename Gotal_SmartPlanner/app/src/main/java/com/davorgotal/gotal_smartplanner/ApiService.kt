package com.davorgotal.gotal_smartplanner

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api/quotes")
    fun getRandomQuote(): Call<List<Quote>>
}
