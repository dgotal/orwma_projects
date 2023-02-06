package com.davorgotal.gotal_smartplanner.network

import com.davorgotal.gotal_smartplanner.model.Quote
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api/quotes")
    fun getRandomQuote(): Call<List<Quote>>
}
