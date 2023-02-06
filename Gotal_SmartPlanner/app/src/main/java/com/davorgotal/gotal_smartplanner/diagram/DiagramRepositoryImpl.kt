package com.davorgotal.gotal_smartplanner.diagram

import android.util.Log
import com.davorgotal.gotal_smartplanner.network.ApiService
import com.davorgotal.gotal_smartplanner.FirebaseDateFormatter
import com.davorgotal.gotal_smartplanner.model.Quote
import com.davorgotal.gotal_smartplanner.model.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DiagramRepositoryImpl {
    private val db = Firebase.firestore
    private var counter = 0

    fun getRandomQuote(listener: QuoteListener) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://type.fit")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getRandomQuote()

        call.enqueue(object : Callback<List<Quote>> {
            override fun onResponse(call: Call<List<Quote>>, response: Response<List<Quote>>) {
                val quotes = response.body()
                val randomIndex = (0 until quotes!!.size).random()
                val randomQuote = quotes[randomIndex]
                listener.getQuote(randomQuote)
            }

            override fun onFailure(call: Call<List<Quote>>, t: Throwable) {
                listener.handleError()
            }
        })
    }

    private fun calculateTimeSpan(startTime: String?, endTime: String?): Int {
        if (endTime != null && startTime != null) {
            val end = FirebaseDateFormatter.timeToMinutes(endTime)
            val start = FirebaseDateFormatter.timeToMinutes(startTime)
            return end - start
        } else {
            return 0
        }
    }

    private fun isToday(date: Date?): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeekStart = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYearStart = calendar.get(Calendar.YEAR)
        val currentDayStart = calendar.get(Calendar.DAY_OF_WEEK)
        if (date != null) {
            calendar.time = date
        }
        val targetWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val targetYear = calendar.get(Calendar.YEAR)
        val targetDay = calendar.get(Calendar.DAY_OF_WEEK)
        return currentWeekStart == targetWeek && currentYearStart == targetYear && currentDayStart == targetDay
    }

     fun calculateTaskPercentageTime(listener: PieChartListener) {
        db.collection("tasks").get()
            .addOnSuccessListener {
                for (data in it.documents) {
                    val task = data.toObject(Task::class.java)
                    if (isToday(FirebaseDateFormatter.stringToDate(task?.date))) {
                        task?.id = data.id
                        counter += calculateTimeSpan(task?.startTime, task?.endTime)
                    }
                }
                    listener.getSum(counter.toFloat())
            }
            .addOnFailureListener {
                Log.e("Task exception", it.message.toString())
            }
    }
}
